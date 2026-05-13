package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class DashboardOverviewVO {
    private UserStatsVO userStats;
    private ProductStatsVO productStats;
    private OrderStatsVO orderStats;
    private TransactionStatsVO transactionStats;
    private CommunityStatsVO communityStats;
    private AfterSalesStatsVO afterSalesStats;
    private DailyStatsVO dailyStats;
}
