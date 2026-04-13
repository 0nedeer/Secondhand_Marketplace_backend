package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerResponseRequest {

    @NotBlank(message = "卖家处理意见不能为空")
    @Size(max = 1000, message = "卖家处理意见长度不能超过1000")
    private String sellerResponse;
}
