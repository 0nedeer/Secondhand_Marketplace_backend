package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAfterSaleRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "订单明细ID不能为空")
    private Long orderItemId;

    @NotBlank(message = "售后类型不能为空")
    private String requestType;

    @NotBlank(message = "申请原因不能为空")
    @Size(max = 255, message = "申请原因长度不能超过255")
    private String requestReason;

    @Size(max = 1000, message = "问题描述长度不能超过1000")
    private String detailDesc;

    private BigDecimal requestedAmount;
}
