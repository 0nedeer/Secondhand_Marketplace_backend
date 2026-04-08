package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "更新论坛标签时使用的参数")
public class TagUpdateDTO {
    
    @NotNull(message = "标签ID不能为空")
    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    
    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone")
    private String tagName;
    
    @Schema(description = "标签图标URL", example = "https://example.com/tag-icon.png")
    private String tagIcon;
    
    @NotNull(message = "排序序号不能为空")
    @Schema(description = "排序序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;
    
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用（0-禁用，1-启用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer isEnabled;
}