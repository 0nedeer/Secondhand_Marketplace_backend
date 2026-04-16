package com.secondhand.marketplace.backend.modules.wallet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdrawal_request")
public class WithdrawalRequest {

    @TableId(type = IdType.AUTO)
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

