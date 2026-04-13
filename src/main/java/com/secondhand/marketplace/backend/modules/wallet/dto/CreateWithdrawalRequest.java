package com.secondhand.marketplace.backend.modules.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateWithdrawalRequest {

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "提现渠道不能为空")
    private String channel;

    @NotBlank(message = "提现账号不能为空")
    @Size(max = 100, message = "提现账号信息长度不能超过100")
    private String channelAccountMask;
}

