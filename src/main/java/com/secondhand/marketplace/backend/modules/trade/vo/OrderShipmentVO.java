package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderShipmentVO {
    private Long id;
    private Long orderId;
    private String shipmentType;
    private String logisticsCompany;
    private String trackingNo;
    private String shipmentStatus;
    private Long shippedBy;
    private LocalDateTime shippedAt;
    private LocalDateTime signedAt;
    private String pickupCode;
    private LocalDateTime pickupVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
