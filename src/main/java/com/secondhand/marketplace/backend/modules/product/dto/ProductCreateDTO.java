package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品创建/草稿入参对象")
public class ProductCreateDTO {
    @Schema(description = "商品分类ID", required = true)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "商品标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "商品副标题")
    private String subtitle;

    @Schema(description = "商品详细描述", required = true)
    @NotBlank(message = "商品描述不能为空")
    private String description;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "新旧程度: new, almost_new, good, fair, poor", required = true)
    @NotBlank(message = "新旧程度不能为空")
    @jakarta.validation.constraints.Pattern(regexp = "^(new|almost_new|good|fair|poor)$", message = "新旧程度必须是 new, almost_new, good, fair, poor 中的一种")
    private String conditionLevel;

    @Schema(description = "购买年份")
    private Integer purchaseYear;

    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Schema(description = "出售价格（元）", required = true)
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal sellingPrice;

    @Schema(description = "交易方式：pickup, shipping, both", required = true)  
    @NotBlank(message = "交易方式不能为空")
    @jakarta.validation.constraints.Pattern(regexp = "^(pickup|shipping|both)$", message = "交易方式必须是 pickup, shipping, both 中的一种")
    private String tradeMode;

    @Schema(description = "自提城市")
    private String pickupCity;

    @Schema(description = "自提地点描述")
    private String pickupAddress;

    @Schema(description = "卖家纬度")
    private BigDecimal locationLat;

    @Schema(description = "卖家经度")
    private BigDecimal locationLng;

    @Schema(description = "是否存为草稿（true为草稿，false直接提交审核）", defaultValue = "false")
    private Boolean isDraft = false;
    
    @Schema(description = "商品图片列表")
    private List<ProductImageDTO> images;
}