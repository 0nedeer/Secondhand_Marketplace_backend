package com.secondhand.marketplace.backend.modules.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.marketplace.backend.modules.dashboard.service.DashboardService;
import com.secondhand.marketplace.backend.modules.dashboard.vo.*;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.PaymentOrderMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentTransaction;
import com.secondhand.marketplace.backend.modules.trade.mapper.PaymentTransactionMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.DisputeCase;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.DisputeCaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.AfterSaleRequestMapper;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPost;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostMapper;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumComment;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumCommentMapper;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumReaction;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumReactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserAccountMapper userAccountMapper;
    private final ProductMapper productMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final DisputeCaseMapper disputeCaseMapper;
    private final AfterSaleRequestMapper afterSaleRequestMapper;
    private final ForumPostMapper forumPostMapper;
    private final ForumCommentMapper forumCommentMapper;
    private final ForumReactionMapper forumReactionMapper;

    @Override
    public DashboardOverviewVO getOverviewData() {
        DashboardOverviewVO overview = new DashboardOverviewVO();
        
        // 用户统计
        overview.setUserStats(getUserStats());
        
        // 商品统计
        overview.setProductStats(getProductStats());
        
        // 订单统计
        overview.setOrderStats(getOrderStats());
        
        // 交易统计
        overview.setTransactionStats(getTransactionStats());
        
        // 社区统计
        overview.setCommunityStats(getCommunityStats());
        
        // 售后统计
        overview.setAfterSalesStats(getAfterSalesStats());
        
        // 每日统计
        overview.setDailyStats(getDailyStats());
        
        return overview;
    }

    private UserStatsVO getUserStats() {
        UserStatsVO stats = new UserStatsVO();
        
        // 总用户数
        stats.setTotalUsers(userAccountMapper.selectCount(null));
        
        // 活跃用户数
        LambdaQueryWrapper<UserAccount> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(UserAccount::getUserStatus, "active");
        stats.setActiveUsers(userAccountMapper.selectCount(activeWrapper));
        
        // 被禁用户数
        LambdaQueryWrapper<UserAccount> bannedWrapper = new LambdaQueryWrapper<>();
        bannedWrapper.eq(UserAccount::getUserStatus, "banned");
        stats.setBannedUsers(userAccountMapper.selectCount(bannedWrapper));
        
        // 今日新用户数
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LambdaQueryWrapper<UserAccount> newUsersWrapper = new LambdaQueryWrapper<>();
        newUsersWrapper.ge(UserAccount::getRegisteredAt, today);
        stats.setNewUsersToday(userAccountMapper.selectCount(newUsersWrapper));
        
        return stats;
    }

    private ProductStatsVO getProductStats() {
        ProductStatsVO stats = new ProductStatsVO();
        
        // 总商品数
        stats.setTotalProducts(productMapper.selectCount(null));
        
        // 在售商品数
        LambdaQueryWrapper<Product> onSaleWrapper = new LambdaQueryWrapper<>();
        onSaleWrapper.eq(Product::getPublishStatus, "on_sale");
        stats.setOnSaleProducts(productMapper.selectCount(onSaleWrapper));
        
        // 待审核商品数
        LambdaQueryWrapper<Product> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Product::getPublishStatus, "pending_review");
        stats.setPendingReviewProducts(productMapper.selectCount(pendingWrapper));
        
        // 已售商品数
        LambdaQueryWrapper<Product> soldWrapper = new LambdaQueryWrapper<>();
        soldWrapper.eq(Product::getPublishStatus, "sold");
        stats.setSoldProducts(productMapper.selectCount(soldWrapper));
        
        // 商品浏览量
        List<Product> products = productMapper.selectList(null);
        long totalViews = products.stream().mapToLong(Product::getViewCount).sum();
        stats.setProductViews(totalViews);
        
        return stats;
    }

    private OrderStatsVO getOrderStats() {
        OrderStatsVO stats = new OrderStatsVO();
        
        // 总订单数
        stats.setTotalOrders(tradeOrderMapper.selectCount(null));
        
        // 待支付订单数
        LambdaQueryWrapper<TradeOrder> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(TradeOrder::getOrderStatus, "pending_payment");
        stats.setPendingPaymentOrders(tradeOrderMapper.selectCount(pendingWrapper));
        
        // 已完成订单数
        LambdaQueryWrapper<TradeOrder> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(TradeOrder::getOrderStatus, "completed");
        stats.setCompletedOrders(tradeOrderMapper.selectCount(completedWrapper));
        
        // 已取消订单数
        LambdaQueryWrapper<TradeOrder> cancelledWrapper = new LambdaQueryWrapper<>();
        cancelledWrapper.eq(TradeOrder::getOrderStatus, "cancelled");
        stats.setCancelledOrders(tradeOrderMapper.selectCount(cancelledWrapper));
        
        // 总销售额和平均订单金额
        LambdaQueryWrapper<PaymentOrder> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(PaymentOrder::getPaymentStatus, "paid");
        List<PaymentOrder> paidOrders = paymentOrderMapper.selectList(paidWrapper);
        double totalSales = paidOrders.stream().mapToDouble(order -> order.getPaidAmount().doubleValue()).sum();
        stats.setTotalSales(totalSales);
        
        if (!paidOrders.isEmpty()) {
            stats.setAverageOrderAmount(totalSales / paidOrders.size());
        }
        
        return stats;
    }

    private TransactionStatsVO getTransactionStats() {
        TransactionStatsVO stats = new TransactionStatsVO();
        
        // 总交易数
        stats.setTotalTransactions(paymentTransactionMapper.selectCount(null));
        
        // 交易金额
        List<PaymentTransaction> transactions = paymentTransactionMapper.selectList(null);
        double totalAmount = transactions.stream().mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum();
        stats.setTransactionAmount(totalAmount);
        
        // 交易成功率
        LambdaQueryWrapper<PaymentTransaction> successWrapper = new LambdaQueryWrapper<>();
        successWrapper.eq(PaymentTransaction::getTransactionStatus, "success");
        long successCount = paymentTransactionMapper.selectCount(successWrapper);
        if (stats.getTotalTransactions() > 0) {
            stats.setTransactionSuccessRate((double) successCount / stats.getTotalTransactions() * 100);
        }
        
        // 纠纷率
        long totalOrders = tradeOrderMapper.selectCount(null);
        long disputeCount = disputeCaseMapper.selectCount(null);
        if (totalOrders > 0) {
            stats.setDisputeRate((double) disputeCount / totalOrders * 100);
        }
        
        return stats;
    }

    private CommunityStatsVO getCommunityStats() {
        CommunityStatsVO stats = new CommunityStatsVO();
        
        // 总帖子数
        stats.setTotalForumPosts(forumPostMapper.selectList().size());
        
        // 已审核帖子数
        long approvedCount = forumPostMapper.countByCondition(null, null, "approved", null, null, null);
        stats.setApprovedForumPosts(approvedCount);
        
        // 待审核帖子数
        long pendingCount = forumPostMapper.countByCondition(null, null, "pending", null, null, null);
        stats.setPendingForumPosts(pendingCount);
        
        // 帖子浏览量
        List<ForumPost> posts = forumPostMapper.selectList();
        long totalViews = posts.stream().mapToLong(ForumPost::getViewCount).sum();
        stats.setForumPostViews(totalViews);
        
        // 总评论数
        stats.setTotalComments(forumCommentMapper.selectList().size());
        
        // 总点赞数
        stats.setTotalLikes(forumReactionMapper.selectList().size());
        
        return stats;
    }

    private AfterSalesStatsVO getAfterSalesStats() {
        AfterSalesStatsVO stats = new AfterSalesStatsVO();
        
        // 总售后数
        stats.setTotalAfterSales(afterSaleRequestMapper.selectCount(null));
        
        // 待处理售后数
        LambdaQueryWrapper<AfterSaleRequest> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.in(AfterSaleRequest::getRequestStatus, "pending_seller", "pending_admin");
        stats.setPendingAfterSales(afterSaleRequestMapper.selectCount(pendingWrapper));
        
        return stats;
    }

    private DailyStatsVO getDailyStats() {
        DailyStatsVO stats = new DailyStatsVO();
        
        // 今日销售额和订单数
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LambdaQueryWrapper<PaymentOrder> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(PaymentOrder::getPaymentStatus, "paid")
                .ge(PaymentOrder::getPaidAt, today);
        List<PaymentOrder> todayOrders = paymentOrderMapper.selectList(todayWrapper);
        
        double dailySales = todayOrders.stream().mapToDouble(order -> order.getPaidAmount().doubleValue()).sum();
        stats.setDailySales(dailySales);
        stats.setDailyOrders(todayOrders.size());
        
        return stats;
    }

    @Override
    public DashboardTrendsVO getTrendsData(String period, String type) {
        DashboardTrendsVO trends = new DashboardTrendsVO();
        
        if (period == null) {
            period = "week";
        }
        
        if (type == null || type.equals("sales")) {
            // 销售趋势
            trends.setWeeklySales(getWeeklySales());
            trends.setMonthlySales(getMonthlySales());
        }
        
        if (type == null || type.equals("orders")) {
            // 订单趋势
            trends.setWeeklyOrders(getWeeklyOrders());
        }
        
        if (type == null || type.equals("community")) {
            // 社区趋势
            trends.setWeeklyCommunity(getWeeklyCommunity());
        }
        
        return trends;
    }

    private List<Double> getWeeklySales() {
        List<Double> weeklySales = new ArrayList<>(7);
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);
            
            LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentOrder::getPaymentStatus, "paid")
                    .between(PaymentOrder::getPaidAt, startOfDay, endOfDay);
            
            List<PaymentOrder> orders = paymentOrderMapper.selectList(wrapper);
            double sales = orders.stream().mapToDouble(order -> order.getPaidAmount().doubleValue()).sum();
            weeklySales.add(sales);
        }
        
        return weeklySales;
    }

    private List<Double> getMonthlySales() {
        List<Double> monthlySales = new ArrayList<>(12);
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 12; i++) {
            LocalDate monthStart = today.withMonth(i + 1).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
            
            LocalDateTime startOfMonth = monthStart.atStartOfDay();
            LocalDateTime endOfMonth = monthEnd.atTime(23, 59, 59, 999999999);
            
            LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentOrder::getPaymentStatus, "paid")
                    .between(PaymentOrder::getPaidAt, startOfMonth, endOfMonth);
            
            List<PaymentOrder> orders = paymentOrderMapper.selectList(wrapper);
            double sales = orders.stream().mapToDouble(order -> order.getPaidAmount().doubleValue()).sum();
            monthlySales.add(sales);
        }
        
        return monthlySales;
    }

    private List<Long> getWeeklyOrders() {
        List<Long> weeklyOrders = new ArrayList<>(7);
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);
            
            LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentOrder::getPaymentStatus, "paid")
                    .between(PaymentOrder::getPaidAt, startOfDay, endOfDay);
            
            long count = paymentOrderMapper.selectCount(wrapper);
            weeklyOrders.add(count);
        }
        
        return weeklyOrders;
    }

    private WeeklyCommunityVO getWeeklyCommunity() {
        WeeklyCommunityVO community = new WeeklyCommunityVO();
        List<Long> posts = new ArrayList<>(7);
        List<Long> comments = new ArrayList<>(7);
        
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // 简化实现：由于ForumMapper没有按日期范围查询的方法，这里使用全量查询后过滤
            List<ForumPost> allPosts = forumPostMapper.selectList();
            long postCount = allPosts.stream()
                    .filter(post -> {
                        LocalDate postDate = post.getCreatedAt().toLocalDate();
                        return postDate.equals(date);
                    })
                    .count();
            posts.add(postCount);
            
            List<ForumComment> allComments = forumCommentMapper.selectList();
            long commentCount = allComments.stream()
                    .filter(comment -> {
                        LocalDate commentDate = comment.getCreatedAt().toLocalDate();
                        return commentDate.equals(date);
                    })
                    .count();
            comments.add(commentCount);
        }
        
        community.setPosts(posts);
        community.setComments(comments);
        return community;
    }
}
