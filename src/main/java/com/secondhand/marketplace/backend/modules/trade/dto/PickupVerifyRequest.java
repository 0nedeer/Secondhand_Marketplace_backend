package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PickupVerifyRequest {

    @NotBlank(message = "自提码不能为空")
    @Size(max = 20, message = "自提码长度不能超过20")
    private String pickupCode;
}
