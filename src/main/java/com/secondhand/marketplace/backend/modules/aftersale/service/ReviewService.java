package com.secondhand.marketplace.backend.modules.aftersale.service;

import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ReplyReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadReviewImageRequest;
import com.secondhand.marketplace.backend.modules.aftersale.vo.ReviewVO;

import java.util.List;

public interface ReviewService {

    Long createReview(Long currentUserId, CreateReviewRequest request);

    List<ReviewVO> listReviews(Long currentUserId, Long sellerId, Long productId, Long orderId);

    ReviewVO getReviewDetail(Long currentUserId, Long reviewId);

    Long uploadReviewImage(Long currentUserId, Long reviewId, UploadReviewImageRequest request);

    void replyReview(Long currentUserId, Long reviewId, ReplyReviewRequest request);
}
