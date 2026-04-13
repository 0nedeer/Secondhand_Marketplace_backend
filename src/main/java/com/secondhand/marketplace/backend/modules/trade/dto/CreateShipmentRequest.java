package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateShipmentRequest {

    @Size(max = 100, message = "物流公司长度不能超过100")
    private String logisticsCompany;

    @Size(max = 100, message = "物流单号长度不能超过100")
    private String trackingNo;

    @Size(max = 20, message = "自提码长度不能超过20")
    private String pickupCode;
}
