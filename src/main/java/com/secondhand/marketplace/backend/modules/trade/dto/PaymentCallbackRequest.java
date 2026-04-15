package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCallbackRequest {

    private Long paymentId;
    private String paymentNo;

    @NotBlank(message = "回调状态不能为空")
    private String paymentStatus;

    private BigDecimal paidAmount;
    private String channelTradeNo;
    private String failedReason;
    private String channelResponse;
}

