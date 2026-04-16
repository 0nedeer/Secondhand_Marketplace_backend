package com.secondhand.marketplace.backend.modules.wallet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualAdjustRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "调整金额不能为空")
    private BigDecimal changeAmount;

    private String note;
}

