package com.secondhand.marketplace.backend.modules.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    // 业务状态常量
    public static final String STATUS_ON_SALE = "on_sale";
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PENDING_REVIEW = "pending_review";
    public static final String STATUS_OFF_SHELF = "off_shelf";
    public static final String STATUS_REJECT = "rejected";
    public static final String STATUS_SOLD = "sold";
    public static final String STATUS_DELETED = "deleted";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String subtitle;
    private String description;
    private String brand;
    private String model;
    private String conditionLevel;
    private Integer purchaseYear;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Boolean canBargain;
    private String tradeMode;
    private String pickupCity;
    private String pickupAddress;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private Integer stock;
    private String publishStatus;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime publishedAt;
    private LocalDateTime offShelfAt;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
