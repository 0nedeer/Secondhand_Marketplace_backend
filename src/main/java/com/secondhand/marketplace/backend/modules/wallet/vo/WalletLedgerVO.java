package com.secondhand.marketplace.backend.modules.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletLedgerVO {
    private Long id;
    private String bizType;
    private Long bizId;
    private BigDecimal changeAmount;
    private BigDecimal balanceAfter;
    private BigDecimal frozenAfter;
    private String note;
    private LocalDateTime createdAt;
}

