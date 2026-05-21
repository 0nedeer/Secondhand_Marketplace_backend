package com.secondhand.marketplace.backend.modules.admin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardOverviewVO {
    private UserStats userStats;
    private ProductStats productStats;
    private OrderStats orderStats;
    private TransactionStats transactionStats;
    private CommunityStats communityStats;
    private AfterSalesStats afterSalesStats;
    private DailyStats dailyStats;

    @Data
    public static class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long bannedUsers;
        private Long newUsersToday;
    }

    @Data
    public static class ProductStats {
        private Long totalProducts;
        private Long onSaleProducts;
        private Long pendingReviewProducts;
        private Long soldProducts;
        private Long productViews;
    }

    @Data
    public static class OrderStats {
        private Long totalOrders;
        private Long pendingPaymentOrders;
        private Long completedOrders;
        private Long cancelledOrders;
        private BigDecimal totalSales;
        private BigDecimal averageOrderAmount;
    }

    @Data
    public static class TransactionStats {
        private Long totalTransactions;
        private BigDecimal transactionAmount;
        private BigDecimal transactionSuccessRate;
        private BigDecimal disputeRate;
    }

    @Data
    public static class CommunityStats {
        private Long totalForumPosts;
        private Long approvedForumPosts;
        private Long pendingForumPosts;
        private Long forumPostViews;
        private Long totalComments;
        private Long totalLikes;
    }

    @Data
    public static class AfterSalesStats {
        private Long totalAfterSales;
        private Long pendingAfterSales;
    }

    @Data
    public static class DailyStats {
        private BigDecimal dailySales;
        private Long dailyOrders;
    }
}
