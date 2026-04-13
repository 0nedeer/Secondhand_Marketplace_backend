package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewImageVO {
    private Long id;
    private Long reviewId;
    private String imageUrl;
    private Integer sortNo;
    private LocalDateTime createdAt;
}
