package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    private Long sellerId;

    @NotBlank(message = "交易方式不能为空")
    private String tradeMode;

    private BigDecimal freightAmount;
    private String remark;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String pickupLocation;

    @Valid
    @NotEmpty(message = "订单商品不能为空")
    private List<CreateOrderItemRequest> items;
}

