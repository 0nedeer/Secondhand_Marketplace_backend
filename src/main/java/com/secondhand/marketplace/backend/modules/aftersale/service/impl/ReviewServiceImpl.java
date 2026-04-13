package com.secondhand.marketplace.backend.modules.aftersale.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ReplyReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadReviewImageRequest;
import com.secondhand.marketplace.backend.modules.aftersale.entity.Review;
import com.secondhand.marketplace.backend.modules.aftersale.entity.ReviewImage;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.ReviewImageMapper;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.ReviewMapper;
import com.secondhand.marketplace.backend.modules.aftersale.service.ReviewService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.ReviewImageVO;
import com.secondhand.marketplace.backend.modules.aftersale.vo.ReviewVO;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderItem;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderItemMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final String ORDER_STATUS_DELIVERED = "delivered";
    private static final String ORDER_STATUS_COMPLETED = "completed";

    private final ReviewMapper reviewMapper;
    private final ReviewImageMapper reviewImageMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long currentUserId, CreateReviewRequest request) {
        TradeOrder order = requireOrder(request.getOrderId());
        if (!currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可提交评价");
        }
        if (!ORDER_STATUS_DELIVERED.equals(order.getOrderStatus()) && !ORDER_STATUS_COMPLETED.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许评价");
        }

        OrderItem item = requireOrderItem(request.getOrderItemId());
        if (!order.getId().equals(item.getOrderId())) {
            throw new BusinessException(400, "订单明细与订单不匹配");
        }
        if (reviewMapper.selectByOrderItemId(item.getId()) != null) {
            throw new BusinessException(400, "该订单明细已评价");
        }

        Review review = new Review();
        review.setOrderId(order.getId());
        review.setOrderItemId(item.getId());
        review.setProductId(item.getProductId());
        review.setBuyerId(order.getBuyerId());
        review.setSellerId(order.getSellerId());
        review.setRating(request.getRating());
        review.setContent(normalizeText(request.getContent()));
        review.setIsAnonymous(Boolean.TRUE.equals(Integer.valueOf(1).equals(request.getIsAnonymous())) ? 1 : 0);
        review.setHasSensitiveContent(0);
        reviewMapper.insert(review);
        return review.getId();
    }

    @Override
    public List<ReviewVO> listReviews(Long currentUserId, Long sellerId, Long productId, Long orderId) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        return reviewMapper.selectByFilters(sellerId, productId, orderId, null)
                .stream()
                .filter(review -> admin || currentUserId.equals(review.getBuyerId()) || currentUserId.equals(review.getSellerId()))
                .map(review -> toReviewVO(review, currentUserId, admin))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewVO getReviewDetail(Long currentUserId, Long reviewId) {
        Review review = requireReview(reviewId);
        assertReviewReadable(currentUserId, review);
        return toReviewVO(review, currentUserId, isAdmin(requireUser(currentUserId)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadReviewImage(Long currentUserId, Long reviewId, UploadReviewImageRequest request) {
        Review review = reviewMapper.selectByIdForUpdate(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评价不存在");
        }
        if (!currentUserId.equals(review.getBuyerId())) {
            throw new BusinessException(403, "仅评价买家可上传评价图片");
        }

        ReviewImage image = new ReviewImage();
        image.setReviewId(reviewId);
        image.setImageUrl(request.getImageUrl().trim());
        image.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        reviewImageMapper.insert(image);
        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long currentUserId, Long reviewId, ReplyReviewRequest request) {
        Review review = reviewMapper.selectByIdForUpdate(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评价不存在");
        }
        if (!currentUserId.equals(review.getSellerId())) {
            throw new BusinessException(403, "仅订单卖家可回复评价");
        }
        if (StringUtils.hasText(review.getSellerReply())) {
            throw new BusinessException(400, "该评价已回复");
        }
        review.setSellerReply(request.getSellerReply().trim());
        review.setSellerReplyAt(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    private Review requireReview(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评价不存在");
        }
        return review;
    }

    private TradeOrder requireOrder(Long orderId) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private OrderItem requireOrderItem(Long orderItemId) {
        OrderItem item = orderItemMapper.selectById(orderItemId);
        if (item == null) {
            throw new BusinessException(404, "订单明细不存在");
        }
        return item;
    }

    private UserAccount requireUser(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private void assertReviewReadable(Long currentUserId, Review review) {
        UserAccount user = requireUser(currentUserId);
        if (!isAdmin(user) && !currentUserId.equals(review.getBuyerId()) && !currentUserId.equals(review.getSellerId())) {
            throw new BusinessException(403, "无权限访问该评价");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private ReviewVO toReviewVO(Review review, Long currentUserId, boolean admin) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setOrderId(review.getOrderId());
        vo.setOrderItemId(review.getOrderItemId());
        vo.setProductId(review.getProductId());
        vo.setBuyerId(review.getIsAnonymous() != null && review.getIsAnonymous() == 1 && !admin && !currentUserId.equals(review.getBuyerId())
                ? null : review.getBuyerId());
        vo.setSellerId(review.getSellerId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setIsAnonymous(review.getIsAnonymous());
        vo.setHasSensitiveContent(review.getHasSensitiveContent());
        vo.setSellerReply(review.getSellerReply());
        vo.setSellerReplyAt(review.getSellerReplyAt());
        vo.setCreatedAt(review.getCreatedAt());
        vo.setImages(reviewImageMapper.selectByReviewId(review.getId()).stream().map(this::toReviewImageVO).collect(Collectors.toList()));
        return vo;
    }

    private ReviewImageVO toReviewImageVO(ReviewImage image) {
        ReviewImageVO vo = new ReviewImageVO();
        vo.setId(image.getId());
        vo.setReviewId(image.getReviewId());
        vo.setImageUrl(image.getImageUrl());
        vo.setSortNo(image.getSortNo());
        vo.setCreatedAt(image.getCreatedAt());
        return vo;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
