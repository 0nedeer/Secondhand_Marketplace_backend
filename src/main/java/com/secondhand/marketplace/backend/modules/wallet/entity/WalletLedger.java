package com.secondhand.marketplace.backend.modules.wallet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wallet_ledger")
public class WalletLedger {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long walletAccountId;
    private String bizType;
    private Long bizId;
    private BigDecimal changeAmount;
    private BigDecimal balanceAfter;
    private BigDecimal frozenAfter;
    private String note;
    private LocalDateTime createdAt;
}

