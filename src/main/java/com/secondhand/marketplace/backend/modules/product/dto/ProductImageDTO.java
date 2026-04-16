package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "商品图片入参对象")
public class ProductImageDTO {
    @Schema(description = "图片URL", required = true)
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    @Schema(description = "是否封面图：0否1是")
    private Boolean isCover;

    @Schema(description = "排序号")
    private Integer sortNo;
}
