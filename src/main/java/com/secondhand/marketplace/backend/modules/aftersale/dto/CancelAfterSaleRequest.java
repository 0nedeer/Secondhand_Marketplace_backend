package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelAfterSaleRequest {

    @Size(max = 1000, message = "取消原因长度不能超过1000")
    private String cancelReason;
}
