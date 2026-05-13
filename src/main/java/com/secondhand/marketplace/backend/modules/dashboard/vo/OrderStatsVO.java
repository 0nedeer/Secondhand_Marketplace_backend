package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class OrderStatsVO {
    private long totalOrders;
    private long pendingPaymentOrders;
    private long completedOrders;
    private long cancelledOrders;
    private double totalSales;
    private double averageOrderAmount;
}
