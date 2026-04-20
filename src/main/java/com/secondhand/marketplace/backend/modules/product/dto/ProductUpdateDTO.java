package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品修改入参对象")
public class ProductUpdateDTO extends ProductCreateDTO {
    @Schema(description = "要修改的商品ID，不传则必须在路径里带")
    private Long id;
}
