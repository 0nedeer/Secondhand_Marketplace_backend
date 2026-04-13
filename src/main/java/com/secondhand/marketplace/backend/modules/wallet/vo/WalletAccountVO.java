package com.secondhand.marketplace.backend.modules.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletAccountVO {
    private Long id;
    private Long userId;
    private String accountStatus;
    private BigDecimal availableBalance;
    private BigDecimal frozenBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalWithdraw;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

