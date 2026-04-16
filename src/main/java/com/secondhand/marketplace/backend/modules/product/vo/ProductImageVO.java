package com.secondhand.marketplace.backend.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品图片VO")
public class ProductImageVO {
    @Schema(description = "图片ID")
    private Long id;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "是否为封面")
    private Boolean isCover;

    @Schema(description = "排序")
    private Integer sortNo;
}
