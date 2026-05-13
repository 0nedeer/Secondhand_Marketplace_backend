package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;
import java.util.List;

@Data
public class DashboardTrendsVO {
    private List<Double> weeklySales;
    private List<Double> monthlySales;
    private List<Long> weeklyOrders;
    private WeeklyCommunityVO weeklyCommunity;
}
