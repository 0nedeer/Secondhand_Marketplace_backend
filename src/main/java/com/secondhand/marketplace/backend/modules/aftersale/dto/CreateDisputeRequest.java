package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDisputeRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long afterSaleId;

    @NotBlank(message = "纠纷说明不能为空")
    @Size(max = 1000, message = "纠纷说明长度不能超过1000")
    private String actionDesc;
}
