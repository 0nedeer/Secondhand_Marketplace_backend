package com.secondhand.marketplace.backend.modules.wallet.service;

import com.secondhand.marketplace.backend.modules.wallet.dto.CreateWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.ManualAdjustRequest;
import com.secondhand.marketplace.backend.modules.wallet.dto.RejectWithdrawalRequest;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletAccountVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WalletLedgerPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalPageVO;
import com.secondhand.marketplace.backend.modules.wallet.vo.WithdrawalVO;

public interface WalletService {

    WalletAccountVO getWalletAccount(Long currentUserId);

    WalletLedgerPageVO getWalletLedger(Long currentUserId, String bizType, long page, long pageSize);

    void manualAdjust(Long currentUserId, ManualAdjustRequest request);

    Long createWithdrawal(Long currentUserId, CreateWithdrawalRequest request);

    WithdrawalPageVO listWithdrawals(Long currentUserId, String status, Long userId, long page, long pageSize);

    WithdrawalVO getWithdrawalDetail(Long currentUserId, Long withdrawalId);

    void approveWithdrawal(Long currentUserId, Long withdrawalId);

    void rejectWithdrawal(Long currentUserId, Long withdrawalId, RejectWithdrawalRequest request);

    void payWithdrawal(Long currentUserId, Long withdrawalId);
}

