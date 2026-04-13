package com.secondhand.marketplace.backend.modules.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_shipment")
public class OrderShipment {

    @TableId(type = IdType.AUTO)
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
