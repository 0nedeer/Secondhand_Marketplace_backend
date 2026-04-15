package com.secondhand.marketplace.backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("seller_follow")
public class SellerFollow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long buyerId;
    private Long sellerId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}