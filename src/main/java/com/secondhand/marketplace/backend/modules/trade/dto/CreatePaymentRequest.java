package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePaymentRequest {

    @NotBlank(message = "支付渠道不能为空")
    private String paymentChannel;
}

