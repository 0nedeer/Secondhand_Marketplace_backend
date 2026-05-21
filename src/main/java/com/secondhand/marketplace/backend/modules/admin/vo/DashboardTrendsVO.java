package com.secondhand.marketplace.backend.modules.admin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardTrendsVO {
    private List<BigDecimal> weeklySales;
    private List<BigDecimal> monthlySales;
    private List<Long> weeklyOrders;
}
