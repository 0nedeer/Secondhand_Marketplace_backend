package com.secondhand.marketplace.backend.modules.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
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
}
