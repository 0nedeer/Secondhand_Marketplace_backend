package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DisputeActionRequest {

    @NotBlank(message = "动作类型不能为空")
    private String actionType;

    @NotBlank(message = "动作说明不能为空")
    @Size(max = 1000, message = "动作说明长度不能超过1000")
    private String actionDesc;

    private String nextStatus;
}
