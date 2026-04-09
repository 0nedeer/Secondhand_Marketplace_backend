package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "更新论坛分类时使用的参数")
public class CategoryUpdateDTO {
    
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    
    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数码产品")
    private String name;
    
    @Schema(description = "分类图标URL", example = "https://example.com/icon.png")
    private String icon;
    
    @NotNull(message = "排序序号不能为空")
    @Schema(description = "排序序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;
    
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用（0-禁用，1-启用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer isEnabled;
    
    @Schema(description = "父分类ID，0表示根分类", example = "0")
    private Long parentId;
}