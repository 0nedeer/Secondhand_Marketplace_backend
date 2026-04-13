package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentTransactionVO {

    private Long id;
    private Long paymentOrderId;
    private String transactionType;
    private String transactionStatus;
    private BigDecimal amount;
    private String channelTradeNo;
    private String channelResponse;
    private LocalDateTime occurredAt;
}

