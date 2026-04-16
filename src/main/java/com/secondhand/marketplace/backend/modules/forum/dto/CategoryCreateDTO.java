package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "创建论坛分类时使用的参数")
public class CategoryCreateDTO {
    
    @Schema(description = "父分类ID（0表示顶级分类）", example = "0")
    private Long parentId;
    
    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数码产品")
    private String name;
    
    @Schema(description = "分类图标URL", example = "https://example.com/icon.png")
    private String icon;
    
    @NotNull(message = "排序序号不能为空")
    @Schema(description = "排序序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;
}