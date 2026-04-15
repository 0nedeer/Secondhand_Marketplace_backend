package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListItemVO {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private String orderStatus;
    private String tradeMode;
    private BigDecimal totalAmount;
    private BigDecimal freightAmount;
    private BigDecimal payAmount;
    private String cancelReason;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private List<OrderItemVO> items;
}

