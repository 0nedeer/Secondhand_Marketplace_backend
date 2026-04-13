package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentOrderVO {

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

