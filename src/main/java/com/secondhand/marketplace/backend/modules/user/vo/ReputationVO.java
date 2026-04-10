package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ReputationVO {
    private Integer creditScore;
    private BigDecimal positiveRate;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer totalReviewCount;
}