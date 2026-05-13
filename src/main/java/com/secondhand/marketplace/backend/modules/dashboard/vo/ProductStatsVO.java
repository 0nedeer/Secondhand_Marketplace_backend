package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class ProductStatsVO {
    private long totalProducts;
    private long onSaleProducts;
    private long pendingReviewProducts;
    private long soldProducts;
    private long productViews;
}
