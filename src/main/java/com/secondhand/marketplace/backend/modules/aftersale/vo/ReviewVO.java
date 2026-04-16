package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private Integer rating;
    private String content;
    private Integer isAnonymous;
    private Integer hasSensitiveContent;
    private String sellerReply;
    private LocalDateTime sellerReplyAt;
    private LocalDateTime createdAt;
    private List<ReviewImageVO> images;
}
