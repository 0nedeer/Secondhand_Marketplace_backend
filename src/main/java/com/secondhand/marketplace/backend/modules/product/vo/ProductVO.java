package com.secondhand.marketplace.backend.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品详情VO")
public class ProductVO {
    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "卖家ID")
    private Long sellerId;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品副标题")
    private String subtitle;

    @Schema(description = "商品详细描述")
    private String description;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "新旧程度")
    private String conditionLevel;

    @Schema(description = "购买年份")
    private Integer purchaseYear;

    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Schema(description = "出售价格（元）")
    private BigDecimal sellingPrice;

    @Schema(description = "是否可议价")
    private Boolean canBargain;

    @Schema(description = "交易方式")
    private String tradeMode;

    @Schema(description = "自提城市")
    private String pickupCity;

    @Schema(description = "自提地点描述")
    private String pickupAddress;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "发布状态")
    private String publishStatus;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "上架时间")
    private LocalDateTime publishedAt;

    @Schema(description = "商品图片列表")
    private List<ProductImageVO> images;
}
