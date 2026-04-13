package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDecisionRequest {

    @NotBlank(message = "管理员裁决状态不能为空")
    private String decisionStatus;

    @Size(max = 1000, message = "管理员裁决意见长度不能超过1000")
    private String adminDecision;

    private BigDecimal finalAmount;
}
