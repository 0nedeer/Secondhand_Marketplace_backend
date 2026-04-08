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
