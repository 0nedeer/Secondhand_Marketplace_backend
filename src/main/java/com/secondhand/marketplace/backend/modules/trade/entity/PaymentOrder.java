package com.secondhand.marketplace.backend.modules.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_order")
public class PaymentOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;
    private String paymentNo;
    private String paymentChannel;
    private String paymentStatus;
    private BigDecimal payableAmount;
    private BigDecimal paidAmount;
    private String channelTradeNo;
    private LocalDateTime paidAt;
    private String failedReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

