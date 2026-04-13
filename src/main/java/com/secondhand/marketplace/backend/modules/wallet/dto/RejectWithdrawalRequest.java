package com.secondhand.marketplace.backend.modules.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectWithdrawalRequest {

    @NotBlank(message = "驳回原因不能为空")
    @Size(max = 255, message = "驳回原因长度不能超过255")
    private String rejectReason;
}

