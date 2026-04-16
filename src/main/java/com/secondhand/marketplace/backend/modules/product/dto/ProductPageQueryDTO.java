package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品分页查询入参")
public class ProductPageQueryDTO {
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "发布状态（选填），默认只查on_sale")
    private String publishStatus = "on_sale";

    @Schema(description = "搜索关键词（标题或描述）")
    private String keyword;
}
