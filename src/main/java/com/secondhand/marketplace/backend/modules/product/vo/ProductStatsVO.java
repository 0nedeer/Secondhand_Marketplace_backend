package com.secondhand.marketplace.backend.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品简要统计VO")
public class ProductStatsVO {
    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "发布状态")
    private String publishStatus;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;
}
