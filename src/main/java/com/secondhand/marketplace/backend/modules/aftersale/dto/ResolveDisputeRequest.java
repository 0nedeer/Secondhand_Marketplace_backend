package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResolveDisputeRequest {

    @NotBlank(message = "责任判定不能为空")
    private String responsibility;

    @NotBlank(message = "裁决结果不能为空")
    @Size(max = 1000, message = "裁决结果长度不能超过1000")
    private String resolutionResult;

    private BigDecimal resolutionAmount;
}
