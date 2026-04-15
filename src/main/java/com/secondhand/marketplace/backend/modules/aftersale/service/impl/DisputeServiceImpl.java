package com.secondhand.marketplace.backend.modules.aftersale.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.DisputeActionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ResolveDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.entity.DisputeActionLog;
import com.secondhand.marketplace.backend.modules.aftersale.entity.DisputeCase;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.AfterSaleRequestMapper;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.DisputeActionLogMapper;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.DisputeCaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.service.DisputeService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.DisputeActionLogVO;
import com.secondhand.marketplace.backend.modules.aftersale.vo.DisputeVO;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {

    private static final Set<String> VALID_ACTION_TYPES = Set.of("submit", "append_evidence", "status_change", "admin_decision", "close");
    private static final Set<String> VALID_STATUSES = Set.of("open", "investigating", "waiting_evidence", "resolved", "closed");
    private static final Set<String> VALID_RESPONSIBILITY = Set.of("buyer", "seller", "both", "platform", "undetermined");

    private final DisputeCaseMapper disputeCaseMapper;
    private final DisputeActionLogMapper disputeActionLogMapper;
    private final AfterSaleRequestMapper afterSaleRequestMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDispute(Long currentUserId, CreateDisputeRequest request) {
        TradeOrder order = requireOrder(request.getOrderId());
        assertOrderPartyOrAdmin(currentUserId, order);

        AfterSaleRequest afterSale = null;
        if (request.getAfterSaleId() != null) {
            afterSale = afterSaleRequestMapper.selectById(request.getAfterSaleId());
            if (afterSale == null) {
                throw new BusinessException(404, "售后申请不存在");
            }
            if (!order.getId().equals(afterSale.getOrderId())) {
                throw new BusinessException(400, "售后申请与订单不匹配");
            }
        }

        if (disputeCaseMapper.selectActiveCase(order.getId(), request.getAfterSaleId()) != null) {
            throw new BusinessException(400, "该订单已存在进行中的纠纷");
        }

        DisputeCase disputeCase = new DisputeCase();
        disputeCase.setDisputeNo(generateDisputeNo());
        disputeCase.setOrderId(order.getId());
        disputeCase.setAfterSaleId(afterSale == null ? null : afterSale.getId());
        disputeCase.setBuyerId(order.getBuyerId());
        disputeCase.setSellerId(order.getSellerId());
        disputeCase.setCurrentStatus("open");
        disputeCase.setResponsibility("undetermined");
        disputeCaseMapper.insert(disputeCase);

        insertActionLog(disputeCase.getId(), currentUserId, "submit", request.getActionDesc().trim());
        return disputeCase.getId();
    }

    @Override
    public List<DisputeVO> listDisputes(Long currentUserId, Long orderId, Long afterSaleId, String status) {
        UserAccount user = requireUser(currentUserId);
        boolean admin = isAdmin(user);
        return disputeCaseMapper.selectByFilters(orderId, afterSaleId, status, null, null)
                .stream()
                .filter(item -> admin || currentUserId.equals(item.getBuyerId()) || currentUserId.equals(item.getSellerId()))
                .map(this::toDisputeVO)
                .collect(Collectors.toList());
    }

    @Override
    public DisputeVO getDisputeDetail(Long currentUserId, Long disputeId) {
        DisputeCase disputeCase = requireDispute(disputeId);
        assertDisputeReadable(currentUserId, disputeCase);
        return toDisputeVO(disputeCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAction(Long currentUserId, Long disputeId, DisputeActionRequest request) {
        DisputeCase disputeCase = requireDisputeForUpdate(disputeId);
        assertDisputeReadable(currentUserId, disputeCase);
        if ("resolved".equals(disputeCase.getCurrentStatus()) || "closed".equals(disputeCase.getCurrentStatus())) {
            throw new BusinessException(400, "当前纠纷状态不允许继续记录动作");
        }
        if (!VALID_ACTION_TYPES.contains(request.getActionType())) {
            throw new BusinessException(400, "不支持的纠纷动作类型");
        }
        if ("admin_decision".equals(request.getActionType()) && !isAdmin(requireUser(currentUserId))) {
            throw new BusinessException(403, "仅管理员可记录管理员裁决动作");
        }

        if ("status_change".equals(request.getActionType())) {
            if (!VALID_STATUSES.contains(request.getNextStatus())) {
                throw new BusinessException(400, "状态变更目标不合法");
            }
            disputeCase.setCurrentStatus(request.getNextStatus());
            disputeCaseMapper.updateById(disputeCase);
        } else if ("close".equals(request.getActionType())) {
            disputeCase.setCurrentStatus("closed");
            disputeCaseMapper.updateById(disputeCase);
        }

        DisputeActionLog log = insertActionLog(disputeId, currentUserId, request.getActionType(), request.getActionDesc().trim());
        return log.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveDispute(Long currentUserId, Long disputeId, ResolveDisputeRequest request) {
        UserAccount currentUser = requireUser(currentUserId);
        if (!isAdmin(currentUser)) {
            throw new BusinessException(403, "仅管理员可裁决纠纷");
        }
        if (!VALID_RESPONSIBILITY.contains(request.getResponsibility())) {
            throw new BusinessException(400, "责任判定不合法");
        }
        if (request.getResolutionAmount() != null && request.getResolutionAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, "裁定金额不能为负数");
        }

        DisputeCase disputeCase = requireDisputeForUpdate(disputeId);
        if ("resolved".equals(disputeCase.getCurrentStatus()) || "closed".equals(disputeCase.getCurrentStatus())) {
            throw new BusinessException(400, "该纠纷已处理完成");
        }

        disputeCase.setCurrentStatus("resolved");
        disputeCase.setResponsibility(request.getResponsibility());
        disputeCase.setResolutionResult(request.getResolutionResult().trim());
        disputeCase.setResolutionAmount(request.getResolutionAmount());
        disputeCase.setResolvedBy(currentUserId);
        disputeCase.setResolvedAt(LocalDateTime.now());
        disputeCaseMapper.updateById(disputeCase);

        insertActionLog(disputeCase.getId(), currentUserId, "admin_decision", request.getResolutionResult().trim());
    }

    private TradeOrder requireOrder(Long orderId) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private DisputeCase requireDispute(Long disputeId) {
        DisputeCase disputeCase = disputeCaseMapper.selectById(disputeId);
        if (disputeCase == null) {
            throw new BusinessException(404, "纠纷不存在");
        }
        return disputeCase;
    }

    private DisputeCase requireDisputeForUpdate(Long disputeId) {
        DisputeCase disputeCase = disputeCaseMapper.selectByIdForUpdate(disputeId);
        if (disputeCase == null) {
            throw new BusinessException(404, "纠纷不存在");
        }
        return disputeCase;
    }

    private UserAccount requireUser(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private void assertOrderPartyOrAdmin(Long currentUserId, TradeOrder order) {
        UserAccount user = requireUser(currentUserId);
        if (!isAdmin(user) && !currentUserId.equals(order.getBuyerId()) && !currentUserId.equals(order.getSellerId())) {
            throw new BusinessException(403, "无权限为该订单创建纠纷");
        }
    }

    private void assertDisputeReadable(Long currentUserId, DisputeCase disputeCase) {
        UserAccount user = requireUser(currentUserId);
        if (!isAdmin(user) && !currentUserId.equals(disputeCase.getBuyerId()) && !currentUserId.equals(disputeCase.getSellerId())) {
            throw new BusinessException(403, "无权限访问该纠纷");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private DisputeActionLog insertActionLog(Long disputeId, Long actionBy, String actionType, String actionDesc) {
        DisputeActionLog log = new DisputeActionLog();
        log.setDisputeId(disputeId);
        log.setActionBy(actionBy);
        log.setActionType(actionType);
        log.setActionDesc(actionDesc);
        disputeActionLogMapper.insert(log);
        return log;
    }

    private DisputeVO toDisputeVO(DisputeCase disputeCase) {
        DisputeVO vo = new DisputeVO();
        vo.setId(disputeCase.getId());
        vo.setDisputeNo(disputeCase.getDisputeNo());
        vo.setOrderId(disputeCase.getOrderId());
        vo.setAfterSaleId(disputeCase.getAfterSaleId());
        vo.setBuyerId(disputeCase.getBuyerId());
        vo.setSellerId(disputeCase.getSellerId());
        vo.setCurrentStatus(disputeCase.getCurrentStatus());
        vo.setResponsibility(disputeCase.getResponsibility());
        vo.setResolutionResult(disputeCase.getResolutionResult());
        vo.setResolutionAmount(disputeCase.getResolutionAmount());
        vo.setResolvedBy(disputeCase.getResolvedBy());
        vo.setResolvedAt(disputeCase.getResolvedAt());
        vo.setCreatedAt(disputeCase.getCreatedAt());
        vo.setUpdatedAt(disputeCase.getUpdatedAt());
        vo.setActions(disputeActionLogMapper.selectByDisputeId(disputeCase.getId()).stream().map(this::toActionVO).collect(Collectors.toList()));
        return vo;
    }

    private DisputeActionLogVO toActionVO(DisputeActionLog log) {
        DisputeActionLogVO vo = new DisputeActionLogVO();
        vo.setId(log.getId());
        vo.setDisputeId(log.getDisputeId());
        vo.setActionBy(log.getActionBy());
        vo.setActionType(log.getActionType());
        vo.setActionDesc(log.getActionDesc());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }

    private String generateDisputeNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "DP" + timePart + random;
    }
}
