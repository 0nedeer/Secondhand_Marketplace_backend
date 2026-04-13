package com.secondhand.marketplace.backend.modules.wallet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wallet_account")
public class WalletAccount {

    @TableId(type = IdType.AUTO)
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

