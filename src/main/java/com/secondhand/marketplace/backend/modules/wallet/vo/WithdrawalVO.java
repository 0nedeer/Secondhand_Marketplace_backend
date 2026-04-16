package com.secondhand.marketplace.backend.modules.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WithdrawalVO {
    private Long id;
    private String withdrawalNo;
    private Long userId;
    private Long walletAccountId;
    private BigDecimal amount;
    private BigDecimal feeAmount;
    private String channel;
    private String channelAccountMask;
    private String withdrawalStatus;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime paidAt;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

