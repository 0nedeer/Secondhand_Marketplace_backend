package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReputationHistoryVO {
    private LocalDate snapshotDate;
    private Integer creditScore;
    private BigDecimal positiveRate;
    private Integer totalOrders;
    private Integer completedOrders;
}