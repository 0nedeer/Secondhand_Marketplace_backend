package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品分页查询入参")
public class ProductPageQueryDTO {
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "发布状态（选填）。非管理员仅允许查询公开状态")
    private String publishStatus;

    @Schema(description = "搜索关键词（标题或描述）")
    private String keyword;

    @Schema(description = "成色级别，如['new','almost_new','good','fair','poor']")
    private List<String> conditionLevels;

    @Schema(description = "发货地")
    private List<String> pickupCities;

    @Schema(description = "交易方式，如'pickup','shipping','both'")
    private String tradeMode;

    @Schema(description = "最低售价")
    private BigDecimal minPrice;

    @Schema(description = "最高售价")
    private BigDecimal maxPrice;

    @Schema(description = "价格排序：asc=升序, desc=降序, 不传=按创建时间倒序")
    private String sortOrder;
}
