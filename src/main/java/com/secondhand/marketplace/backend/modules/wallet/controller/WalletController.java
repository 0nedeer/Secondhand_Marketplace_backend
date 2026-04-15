package com.secondhand.marketplace.backend.modules.wallet.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.wallet.dto.CreateWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.ManualAdjustRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.RejectWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.service.WalletService;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletAccountVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletLedgerPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/account")
    public CommonResult<WalletAccountVO> getAccount() {
        return CommonResult.success(walletService.getWalletAccount(requireLogin()));
    }

    @GetMapping("/ledger")
    public CommonResult<WalletLedgerPageVO> getLedger(
            @RequestParam(required = false) String bizType,
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) long pageSize) {
        return CommonResult.success(walletService.getWalletLedger(requireLogin(), bizType, page, pageSize));
    }

    @PostMapping("/manual-adjust")
    public CommonResult<Void> manualAdjust(@Valid @RequestBody ManualAdjustRequest request) {
        walletService.manualAdjust(requireLogin(), request);
        return CommonResult.success();
    }

    @PostMapping("/withdrawals")
    public CommonResult<Long> createWithdrawal(@Valid @RequestBody CreateWithdrawalRequest request) {
        return CommonResult.success(walletService.createWithdrawal(requireLogin(), request));
    }

    @GetMapping("/withdrawals")
    public CommonResult<WithdrawalPageVO> listWithdrawals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) long pageSize) {
        return CommonResult.success(walletService.listWithdrawals(requireLogin(), status, userId, page, pageSize));
    }

    @GetMapping("/withdrawals/{withdrawalId}")
    public CommonResult<WithdrawalVO> getWithdrawalDetail(@PathVariable Long withdrawalId) {
        return CommonResult.success(walletService.getWithdrawalDetail(requireLogin(), withdrawalId));
    }

    @PostMapping("/withdrawals/{withdrawalId}/approve")
    public CommonResult<Void> approveWithdrawal(@PathVariable Long withdrawalId) {
        walletService.approveWithdrawal(requireLogin(), withdrawalId);
        return CommonResult.success();
    }

    @PostMapping("/withdrawals/{withdrawalId}/reject")
    public CommonResult<Void> rejectWithdrawal(@PathVariable Long withdrawalId,
                                               @Valid @RequestBody RejectWithdrawalRequest request) {
        walletService.rejectWithdrawal(requireLogin(), withdrawalId, request);
        return CommonResult.success();
    }

    @PostMapping("/withdrawals/{withdrawalId}/pay")
    public CommonResult<Void> payWithdrawal(@PathVariable Long withdrawalId) {
        walletService.payWithdrawal(requireLogin(), withdrawalId);
        return CommonResult.success();
    }

    private Long requireLogin() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}

