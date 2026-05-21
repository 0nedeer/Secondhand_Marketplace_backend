package com.secondhand.marketplace.backend.modules.admin.service.impl;

import com.secondhand.marketplace.backend.modules.admin.service.AdminAuthService;
import com.secondhand.marketplace.backend.modules.admin.service.DashboardService;
import com.secondhand.marketplace.backend.modules.admin.vo.DashboardOverviewVO;
import com.secondhand.marketplace.backend.modules.admin.vo.DashboardTrendsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final JdbcTemplate jdbcTemplate;
    private final AdminAuthService adminAuthService;

    @Override
    public DashboardOverviewVO getOverview(Long adminId) {
        adminAuthService.requireAdmin(adminId);

        DashboardOverviewVO overview = new DashboardOverviewVO();

        DashboardOverviewVO.UserStats userStats = new DashboardOverviewVO.UserStats();
        userStats.setTotalUsers(count("SELECT COUNT(*) FROM user_account"));
        userStats.setActiveUsers(count("SELECT COUNT(*) FROM user_account WHERE user_status = 'active'"));
        userStats.setBannedUsers(count("SELECT COUNT(*) FROM user_account WHERE user_status = 'banned'"));
        userStats.setNewUsersToday(count("SELECT COUNT(*) FROM user_account WHERE DATE(registered_at) = CURDATE()"));
        overview.setUserStats(userStats);

        DashboardOverviewVO.ProductStats productStats = new DashboardOverviewVO.ProductStats();
        productStats.setTotalProducts(count("SELECT COUNT(*) FROM product"));
        productStats.setOnSaleProducts(count("SELECT COUNT(*) FROM product WHERE publish_status = 'on_sale'"));
        productStats.setPendingReviewProducts(count("SELECT COUNT(*) FROM product WHERE publish_status = 'pending_review'"));
        productStats.setSoldProducts(count("SELECT COUNT(*) FROM product WHERE publish_status = 'sold'"));
        productStats.setProductViews(count("SELECT COALESCE(SUM(view_count), 0) FROM product"));
        overview.setProductStats(productStats);

        DashboardOverviewVO.OrderStats orderStats = new DashboardOverviewVO.OrderStats();
        orderStats.setTotalOrders(count("SELECT COUNT(*) FROM trade_order"));
        orderStats.setPendingPaymentOrders(count("SELECT COUNT(*) FROM trade_order WHERE order_status = 'pending_payment'"));
        orderStats.setCompletedOrders(count("SELECT COUNT(*) FROM trade_order WHERE order_status = 'completed'"));
        orderStats.setCancelledOrders(count("SELECT COUNT(*) FROM trade_order WHERE order_status = 'cancelled'"));
        orderStats.setTotalSales(money("SELECT COALESCE(SUM(pay_amount), 0) FROM trade_order WHERE order_status IN ('completed','delivered','shipped','paid_pending_ship')"));
        orderStats.setAverageOrderAmount(money("SELECT COALESCE(AVG(pay_amount), 0) FROM trade_order WHERE order_status IN ('completed','delivered','shipped','paid_pending_ship')"));
        overview.setOrderStats(orderStats);

        Long paymentTotal = count("SELECT COUNT(*) FROM payment_order");
        Long paymentPaid = count("SELECT COUNT(*) FROM payment_order WHERE payment_status = 'paid'");
        DashboardOverviewVO.TransactionStats transactionStats = new DashboardOverviewVO.TransactionStats();
        transactionStats.setTotalTransactions(paymentPaid);
        transactionStats.setTransactionAmount(money("SELECT COALESCE(SUM(paid_amount), 0) FROM payment_order WHERE payment_status = 'paid'"));
        transactionStats.setTransactionSuccessRate(ratio(paymentPaid, paymentTotal));
        Long disputeTotal = count("SELECT COUNT(*) FROM dispute_case");
        transactionStats.setDisputeRate(ratio(disputeTotal, Math.max(overview.getOrderStats().getTotalOrders(), 1L)));
        overview.setTransactionStats(transactionStats);

        DashboardOverviewVO.CommunityStats communityStats = new DashboardOverviewVO.CommunityStats();
        communityStats.setTotalForumPosts(count("SELECT COUNT(*) FROM forum_post WHERE is_deleted = 0"));
        communityStats.setApprovedForumPosts(count("SELECT COUNT(*) FROM forum_post WHERE is_deleted = 0 AND audit_status = 'approved'"));
        communityStats.setPendingForumPosts(count("SELECT COUNT(*) FROM forum_post WHERE is_deleted = 0 AND audit_status = 'pending'"));
        communityStats.setForumPostViews(count("SELECT COALESCE(SUM(view_count), 0) FROM forum_post WHERE is_deleted = 0"));
        communityStats.setTotalComments(count("SELECT COUNT(*) FROM forum_comment WHERE is_deleted = 0"));
        communityStats.setTotalLikes(count("SELECT COALESCE(SUM(like_count), 0) FROM forum_post WHERE is_deleted = 0")
                + count("SELECT COALESCE(SUM(like_count), 0) FROM forum_comment WHERE is_deleted = 0"));
        overview.setCommunityStats(communityStats);

        DashboardOverviewVO.AfterSalesStats afterSalesStats = new DashboardOverviewVO.AfterSalesStats();
        afterSalesStats.setTotalAfterSales(count("SELECT COUNT(*) FROM after_sale_request"));
        afterSalesStats.setPendingAfterSales(count("SELECT COUNT(*) FROM after_sale_request WHERE request_status IN ('pending_seller','pending_admin')"));
        overview.setAfterSalesStats(afterSalesStats);

        DashboardOverviewVO.DailyStats dailyStats = new DashboardOverviewVO.DailyStats();
        dailyStats.setDailySales(money("SELECT COALESCE(SUM(pay_amount), 0) FROM trade_order WHERE DATE(created_at) = CURDATE() AND order_status IN ('completed','delivered','shipped','paid_pending_ship')"));
        dailyStats.setDailyOrders(count("SELECT COUNT(*) FROM trade_order WHERE DATE(created_at) = CURDATE()"));
        overview.setDailyStats(dailyStats);

        return overview;
    }

    @Override
    public DashboardTrendsVO getTrends(Long adminId) {
        adminAuthService.requireAdmin(adminId);
        DashboardTrendsVO trends = new DashboardTrendsVO();
        trends.setWeeklySales(new ArrayList<>());
        trends.setWeeklyOrders(new ArrayList<>());
        trends.setMonthlySales(new ArrayList<>());

        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            trends.getWeeklySales().add(money("SELECT COALESCE(SUM(pay_amount), 0) FROM trade_order WHERE DATE(created_at) = ? AND order_status IN ('completed','delivered','shipped','paid_pending_ship')", day));
            trends.getWeeklyOrders().add(count("SELECT COUNT(*) FROM trade_order WHERE DATE(created_at) = ?", day));
        }
        for (int i = 11; i >= 0; i--) {
            LocalDate month = today.minusMonths(i).withDayOfMonth(1);
            trends.getMonthlySales().add(money("SELECT COALESCE(SUM(pay_amount), 0) FROM trade_order WHERE DATE_FORMAT(created_at, '%Y-%m') = ? AND order_status IN ('completed','delivered','shipped','paid_pending_ship')", month.toString().substring(0, 7)));
        }
        return trends;
    }

    private Long count(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0L : value;
    }

    private BigDecimal money(String sql, Object... args) {
        BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class, args);
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal ratio(Long numerator, Long denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator == null ? 0 : numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }
}
