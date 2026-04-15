package com.secondhand.marketplace.backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("seller_reputation_snapshot")
public class SellerReputationSnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sellerId;
    private LocalDate snapshotDate;
    private Integer creditScore;
    private BigDecimal positiveRate;
    private Integer totalOrders;
    private Integer completedOrders;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}