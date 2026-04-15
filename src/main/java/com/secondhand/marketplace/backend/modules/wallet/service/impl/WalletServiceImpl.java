package com.secondhand.marketplace.backend.modules.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import com.secondhand.marketplace.backend.modules.wallet.dto.CreateWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.ManualAdjustRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.RejectWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.entity.WalletAccount;
import com.secondhand.marketplace.backend.modules.wallet.entity.WalletLedger;
import com.secondhand.marketplace.backend.modules.wallet.entity.WithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.mapper.WalletAccountMapper;
import com.secondhand.marketplace.backend.modules.wallet.mapper.WalletLedgerMapper;
import com.secondhand.marketplace.backend.modules.wallet.mapper.WithdrawalRequestMapper;
import com.secondhand.marketplace.backend.modules.wallet.service.WalletService;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletAccountVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletLedgerPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletLedgerVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private static final String ACCOUNT_STATUS_ACTIVE = "active";
    private static final String ACCOUNT_STATUS_CLOSED = "closed";

    private static final String LEDGER_BIZ_WITHDRAW_FREEZE = "withdraw_freeze";
    private static final String LEDGER_BIZ_WITHDRAW_SUCCESS = "withdraw_success";
    private static final String LEDGER_BIZ_WITHDRAW_REJECT = "withdraw_reject";
    private static final String LEDGER_BIZ_MANUAL_ADJUST = "manual_adjust";

    private static final String WITHDRAW_STATUS_PENDING = "pending";
    private static final String WITHDRAW_STATUS_APPROVED = "approved";
    private static final String WITHDRAW_STATUS_REJECTED = "rejected";
    private static final String WITHDRAW_STATUS_PROCESSING = "processing";
    private static final String WITHDRAW_STATUS_PAID = "paid";

    private static final Set<String> VALID_LEDGER_BIZ_TYPES = Set.of(
            "order_income", "refund_out", LEDGER_BIZ_WITHDRAW_FREEZE,
            LEDGER_BIZ_WITHDRAW_SUCCESS, LEDGER_BIZ_WITHDRAW_REJECT, LEDGER_BIZ_MANUAL_ADJUST
    );
    private static final Set<String> VALID_WITHDRAW_CHANNELS = Set.of("wechat", "alipay", "bank_card");

    private final WalletAccountMapper walletAccountMapper;
    private final WalletLedgerMapper walletLedgerMapper;
    private final WithdrawalRequestMapper withdrawalRequestMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletAccountVO getWalletAccount(Long currentUserId) {
        UserAccount currentUser = requireUser(currentUserId);
        requireSeller(currentUser);
        WalletAccount walletAccount = getOrCreateWalletAccountForUpdate(currentUserId);
        return toWalletAccountVO(walletAccount);
    }

    @Override
    public WalletLedgerPageVO getWalletLedger(Long currentUserId, String bizType, long page, long pageSize) {
        UserAccount currentUser = requireUser(currentUserId);
        requireSeller(currentUser);
        WalletAccount walletAccount = getOrCreateWalletAccount(currentUserId);

        if (StringUtils.hasText(bizType) && !VALID_LEDGER_BIZ_TYPES.contains(bizType)) {
            throw new BusinessException(400, "不支持的流水业务类型");
        }

        LambdaQueryWrapper<WalletLedger> wrapper = new LambdaQueryWrapper<WalletLedger>()
                .eq(WalletLedger::getWalletAccountId, walletAccount.getId())
                .orderByDesc(WalletLedger::getCreatedAt)
                .orderByDesc(WalletLedger::getId);
        if (StringUtils.hasText(bizType)) {
            wrapper.eq(WalletLedger::getBizType, bizType);
        }

        Page<WalletLedger> ledgerPage = walletLedgerMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<WalletLedgerVO> list = ledgerPage.getRecords().stream().map(this::toWalletLedgerVO).collect(Collectors.toList());
        return new WalletLedgerPageVO(ledgerPage.getTotal(), ledgerPage.getCurrent(), ledgerPage.getSize(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualAdjust(Long currentUserId, ManualAdjustRequest request) {
        UserAccount operator = requireUser(currentUserId);
        requireAdmin(operator);

        if (request.getChangeAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(400, "调整金额不能为0");
        }

        UserAccount targetUser = requireUser(request.getUserId());
        WalletAccount walletAccount = getOrCreateWalletAccountForUpdate(targetUser.getId());
        if (ACCOUNT_STATUS_CLOSED.equals(walletAccount.getAccountStatus())) {
            throw new BusinessException("目标钱包账户已关闭，无法调整");
        }

        BigDecimal newAvailable = walletAccount.getAvailableBalance().add(request.getChangeAmount());
        if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("调整后可用余额不能小于0");
        }

        walletAccount.setAvailableBalance(newAvailable);
        walletAccountMapper.updateById(walletAccount);

        insertLedger(walletAccount, LEDGER_BIZ_MANUAL_ADJUST, null, request.getChangeAmount(), request.getNote());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdrawal(Long currentUserId, CreateWithdrawalRequest request) {
        UserAccount currentUser = requireUser(currentUserId);
        requireSeller(currentUser);
        validateWithdrawalChannel(request.getChannel());

        WalletAccount walletAccount = getOrCreateWalletAccountForUpdate(currentUserId);
        if (!ACCOUNT_STATUS_ACTIVE.equals(walletAccount.getAccountStatus())) {
            throw new BusinessException("钱包状态不可提现");
        }
        if (walletAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("可用余额不足");
        }

        walletAccount.setAvailableBalance(walletAccount.getAvailableBalance().subtract(request.getAmount()));
        walletAccount.setFrozenBalance(walletAccount.getFrozenBalance().add(request.getAmount()));
        walletAccountMapper.updateById(walletAccount);

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setWithdrawalNo(generateWithdrawalNo());
        withdrawalRequest.setUserId(currentUserId);
        withdrawalRequest.setWalletAccountId(walletAccount.getId());
        withdrawalRequest.setAmount(request.getAmount());
        withdrawalRequest.setFeeAmount(BigDecimal.ZERO);
        withdrawalRequest.setChannel(request.getChannel());
        withdrawalRequest.setChannelAccountMask(request.getChannelAccountMask());
        withdrawalRequest.setWithdrawalStatus(WITHDRAW_STATUS_PENDING);
        withdrawalRequestMapper.insert(withdrawalRequest);

        insertLedger(walletAccount, LEDGER_BIZ_WITHDRAW_FREEZE, withdrawalRequest.getId(), request.getAmount().negate(), "发起提现，冻结资金");
        return withdrawalRequest.getId();
    }

    @Override
    public WithdrawalPageVO listWithdrawals(Long currentUserId, String status, Long userId, long page, long pageSize) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        if (!admin) {
            requireSeller(currentUser);
        }

        LambdaQueryWrapper<WithdrawalRequest> wrapper = new LambdaQueryWrapper<>();
        if (!admin) {
            wrapper.eq(WithdrawalRequest::getUserId, currentUserId);
        } else if (userId != null) {
            wrapper.eq(WithdrawalRequest::getUserId, userId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(WithdrawalRequest::getWithdrawalStatus, status);
        }
        wrapper.orderByDesc(WithdrawalRequest::getCreatedAt).orderByDesc(WithdrawalRequest::getId);

        Page<WithdrawalRequest> requestPage = withdrawalRequestMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<WithdrawalVO> list = requestPage.getRecords().stream().map(this::toWithdrawalVO).collect(Collectors.toList());
        return new WithdrawalPageVO(requestPage.getTotal(), requestPage.getCurrent(), requestPage.getSize(), list);
    }

    @Override
    public WithdrawalVO getWithdrawalDetail(Long currentUserId, Long withdrawalId) {
        UserAccount currentUser = requireUser(currentUserId);
        WithdrawalRequest withdrawalRequest = requireWithdrawal(withdrawalId);
        if (!isAdmin(currentUser) && !currentUserId.equals(withdrawalRequest.getUserId())) {
            throw new BusinessException(403, "无权限查看该提现单");
        }
        return toWithdrawalVO(withdrawalRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveWithdrawal(Long currentUserId, Long withdrawalId) {
        UserAccount operator = requireUser(currentUserId);
        requireAdmin(operator);

        WithdrawalRequest withdrawalRequest = requireWithdrawalForUpdate(withdrawalId);
        if (!WITHDRAW_STATUS_PENDING.equals(withdrawalRequest.getWithdrawalStatus())) {
            throw new BusinessException("当前提现状态不允许审核通过");
        }

        withdrawalRequest.setWithdrawalStatus(WITHDRAW_STATUS_APPROVED);
        withdrawalRequest.setReviewedBy(currentUserId);
        withdrawalRequest.setReviewedAt(LocalDateTime.now());
        withdrawalRequest.setRejectReason(null);
        withdrawalRequestMapper.updateById(withdrawalRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectWithdrawal(Long currentUserId, Long withdrawalId, RejectWithdrawalRequest request) {
        UserAccount operator = requireUser(currentUserId);
        requireAdmin(operator);

        WithdrawalRequest withdrawalRequest = requireWithdrawalForUpdate(withdrawalId);
        if (!WITHDRAW_STATUS_PENDING.equals(withdrawalRequest.getWithdrawalStatus())
                && !WITHDRAW_STATUS_APPROVED.equals(withdrawalRequest.getWithdrawalStatus())) {
            throw new BusinessException("当前提现状态不允许驳回");
        }

        WalletAccount walletAccount = walletAccountMapper.selectByIdForUpdate(withdrawalRequest.getWalletAccountId());
        if (walletAccount == null) {
            throw new BusinessException("钱包账户不存在");
        }
        if (walletAccount.getFrozenBalance().compareTo(withdrawalRequest.getAmount()) < 0) {
            throw new BusinessException("冻结余额不足，无法驳回提现");
        }

        walletAccount.setFrozenBalance(walletAccount.getFrozenBalance().subtract(withdrawalRequest.getAmount()));
        walletAccount.setAvailableBalance(walletAccount.getAvailableBalance().add(withdrawalRequest.getAmount()));
        walletAccountMapper.updateById(walletAccount);

        withdrawalRequest.setWithdrawalStatus(WITHDRAW_STATUS_REJECTED);
        withdrawalRequest.setReviewedBy(currentUserId);
        withdrawalRequest.setReviewedAt(LocalDateTime.now());
        withdrawalRequest.setRejectReason(request.getRejectReason());
        withdrawalRequestMapper.updateById(withdrawalRequest);

        insertLedger(walletAccount, LEDGER_BIZ_WITHDRAW_REJECT, withdrawalRequest.getId(), withdrawalRequest.getAmount(), request.getRejectReason());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payWithdrawal(Long currentUserId, Long withdrawalId) {
        UserAccount operator = requireUser(currentUserId);
        requireAdmin(operator);

        WithdrawalRequest withdrawalRequest = requireWithdrawalForUpdate(withdrawalId);
        if (!WITHDRAW_STATUS_APPROVED.equals(withdrawalRequest.getWithdrawalStatus())
                && !WITHDRAW_STATUS_PROCESSING.equals(withdrawalRequest.getWithdrawalStatus())) {
            throw new BusinessException("当前提现状态不允许打款");
        }

        WalletAccount walletAccount = walletAccountMapper.selectByIdForUpdate(withdrawalRequest.getWalletAccountId());
        if (walletAccount == null) {
            throw new BusinessException("钱包账户不存在");
        }
        if (walletAccount.getFrozenBalance().compareTo(withdrawalRequest.getAmount()) < 0) {
            throw new BusinessException("冻结余额不足，无法完成打款");
        }

        walletAccount.setFrozenBalance(walletAccount.getFrozenBalance().subtract(withdrawalRequest.getAmount()));
        walletAccount.setTotalWithdraw(walletAccount.getTotalWithdraw().add(withdrawalRequest.getAmount()));
        walletAccountMapper.updateById(walletAccount);

        if (withdrawalRequest.getReviewedBy() == null) {
            withdrawalRequest.setReviewedBy(currentUserId);
            withdrawalRequest.setReviewedAt(LocalDateTime.now());
        }
        withdrawalRequest.setWithdrawalStatus(WITHDRAW_STATUS_PAID);
        withdrawalRequest.setPaidAt(LocalDateTime.now());
        withdrawalRequestMapper.updateById(withdrawalRequest);

        insertLedger(walletAccount, LEDGER_BIZ_WITHDRAW_SUCCESS, withdrawalRequest.getId(), BigDecimal.ZERO, "提现打款成功");
    }

    private WalletAccount getOrCreateWalletAccount(Long userId) {
        WalletAccount walletAccount = walletAccountMapper.selectByUserId(userId);
        if (walletAccount != null) {
            return walletAccount;
        }
        createWalletAccountIfAbsent(userId);
        WalletAccount created = walletAccountMapper.selectByUserId(userId);
        if (created == null) {
            throw new BusinessException("钱包账户初始化失败");
        }
        return created;
    }

    private WalletAccount getOrCreateWalletAccountForUpdate(Long userId) {
        WalletAccount walletAccount = walletAccountMapper.selectByUserIdForUpdate(userId);
        if (walletAccount != null) {
            return walletAccount;
        }
        createWalletAccountIfAbsent(userId);
        WalletAccount created = walletAccountMapper.selectByUserIdForUpdate(userId);
        if (created == null) {
            throw new BusinessException("钱包账户初始化失败");
        }
        return created;
    }

    private void createWalletAccountIfAbsent(Long userId) {
        WalletAccount walletAccount = new WalletAccount();
        walletAccount.setUserId(userId);
        walletAccount.setAccountStatus(ACCOUNT_STATUS_ACTIVE);
        walletAccount.setAvailableBalance(BigDecimal.ZERO);
        walletAccount.setFrozenBalance(BigDecimal.ZERO);
        walletAccount.setTotalIncome(BigDecimal.ZERO);
        walletAccount.setTotalWithdraw(BigDecimal.ZERO);
        try {
            walletAccountMapper.insert(walletAccount);
        } catch (DuplicateKeyException ignored) {
            // 并发场景下已有线程创建成功，直接忽略后重新查询
        }
    }

    private void insertLedger(WalletAccount walletAccount, String bizType, Long bizId, BigDecimal changeAmount, String note) {
        WalletLedger ledger = new WalletLedger();
        ledger.setWalletAccountId(walletAccount.getId());
        ledger.setBizType(bizType);
        ledger.setBizId(bizId);
        ledger.setChangeAmount(changeAmount);
        ledger.setBalanceAfter(walletAccount.getAvailableBalance());
        ledger.setFrozenAfter(walletAccount.getFrozenBalance());
        ledger.setNote(note);
        walletLedgerMapper.insert(ledger);
    }

    private WithdrawalRequest requireWithdrawal(Long withdrawalId) {
        WithdrawalRequest request = withdrawalRequestMapper.selectById(withdrawalId);
        if (request == null) {
            throw new BusinessException(404, "提现申请不存在");
        }
        return request;
    }

    private WithdrawalRequest requireWithdrawalForUpdate(Long withdrawalId) {
        WithdrawalRequest request = withdrawalRequestMapper.selectByIdForUpdate(withdrawalId);
        if (request == null) {
            throw new BusinessException(404, "提现申请不存在");
        }
        return request;
    }

    private void validateWithdrawalChannel(String channel) {
        if (!VALID_WITHDRAW_CHANNELS.contains(channel)) {
            throw new BusinessException(400, "不支持的提现渠道");
        }
    }

    private UserAccount requireUser(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private void requireSeller(UserAccount user) {
        if (user == null || !Integer.valueOf(1).equals(user.getCanSell())) {
            throw new BusinessException(403, "仅卖家可访问该接口");
        }
    }

    private void requireAdmin(UserAccount user) {
        if (!isAdmin(user)) {
            throw new BusinessException(403, "仅管理员可执行该操作");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private WalletAccountVO toWalletAccountVO(WalletAccount account) {
        WalletAccountVO vo = new WalletAccountVO();
        vo.setId(account.getId());
        vo.setUserId(account.getUserId());
        vo.setAccountStatus(account.getAccountStatus());
        vo.setAvailableBalance(account.getAvailableBalance());
        vo.setFrozenBalance(account.getFrozenBalance());
        vo.setTotalIncome(account.getTotalIncome());
        vo.setTotalWithdraw(account.getTotalWithdraw());
        vo.setCreatedAt(account.getCreatedAt());
        vo.setUpdatedAt(account.getUpdatedAt());
        return vo;
    }

    private WalletLedgerVO toWalletLedgerVO(WalletLedger ledger) {
        WalletLedgerVO vo = new WalletLedgerVO();
        vo.setId(ledger.getId());
        vo.setBizType(ledger.getBizType());
        vo.setBizId(ledger.getBizId());
        vo.setChangeAmount(ledger.getChangeAmount());
        vo.setBalanceAfter(ledger.getBalanceAfter());
        vo.setFrozenAfter(ledger.getFrozenAfter());
        vo.setNote(ledger.getNote());
        vo.setCreatedAt(ledger.getCreatedAt());
        return vo;
    }

    private WithdrawalVO toWithdrawalVO(WithdrawalRequest request) {
        WithdrawalVO vo = new WithdrawalVO();
        vo.setId(request.getId());
        vo.setWithdrawalNo(request.getWithdrawalNo());
        vo.setUserId(request.getUserId());
        vo.setWalletAccountId(request.getWalletAccountId());
        vo.setAmount(request.getAmount());
        vo.setFeeAmount(request.getFeeAmount());
        vo.setChannel(request.getChannel());
        vo.setChannelAccountMask(request.getChannelAccountMask());
        vo.setWithdrawalStatus(request.getWithdrawalStatus());
        vo.setReviewedBy(request.getReviewedBy());
        vo.setReviewedAt(request.getReviewedAt());
        vo.setPaidAt(request.getPaidAt());
        vo.setRejectReason(request.getRejectReason());
        vo.setCreatedAt(request.getCreatedAt());
        vo.setUpdatedAt(request.getUpdatedAt());
        return vo;
    }

    private String generateWithdrawalNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "WD" + timePart + random;
    }
}

