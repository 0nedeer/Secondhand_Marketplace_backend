package com.secondhand.marketplace.backend.modules.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review_image")
public class ReviewImage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reviewId;
    private String imageUrl;
    private Integer sortNo;
    private LocalDateTime createdAt;
}
