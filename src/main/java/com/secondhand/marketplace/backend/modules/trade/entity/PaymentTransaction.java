package com.secondhand.marketplace.backend.modules.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_transaction")
public class PaymentTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long paymentOrderId;
    private String transactionType;
    private String transactionStatus;
    private BigDecimal amount;
    private String channelTradeNo;
    private String channelResponse;
    private LocalDateTime occurredAt;
}

