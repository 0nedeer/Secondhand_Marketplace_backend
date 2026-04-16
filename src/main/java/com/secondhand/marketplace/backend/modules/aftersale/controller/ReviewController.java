package com.secondhand.marketplace.backend.modules.aftersale.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ReplyReviewRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadReviewImageRequest;
import com.secondhand.marketplace.backend.modules.aftersale.service.ReviewService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.ReviewVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public CommonResult<Long> createReview(@Valid @RequestBody CreateReviewRequest request) {
        return CommonResult.success(reviewService.createReview(requireLogin(), request));
    }

    @GetMapping
    public CommonResult<List<ReviewVO>> listReviews(@RequestParam(required = false) Long sellerId,
                                                    @RequestParam(required = false) Long productId,
                                                    @RequestParam(required = false) Long orderId) {
        return CommonResult.success(reviewService.listReviews(requireLogin(), sellerId, productId, orderId));
    }

    @GetMapping("/{reviewId}")
    public CommonResult<ReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        return CommonResult.success(reviewService.getReviewDetail(requireLogin(), reviewId));
    }

    @PostMapping("/{reviewId}/images")
    public CommonResult<Long> uploadReviewImage(@PathVariable Long reviewId, @Valid @RequestBody UploadReviewImageRequest request) {
        return CommonResult.success(reviewService.uploadReviewImage(requireLogin(), reviewId, request));
    }

    @PostMapping("/{reviewId}/reply")
    public CommonResult<Void> replyReview(@PathVariable Long reviewId, @Valid @RequestBody ReplyReviewRequest request) {
        reviewService.replyReview(requireLogin(), reviewId, request);
        return CommonResult.success();
    }

    private Long requireLogin() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
