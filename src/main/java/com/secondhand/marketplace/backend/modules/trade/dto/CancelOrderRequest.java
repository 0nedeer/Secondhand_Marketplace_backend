package com.secondhand.marketplace.backend.modules.trade.dto;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String cancelReason;
}

