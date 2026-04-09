package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "更新帖子时使用的参数")
public class PostUpdateDTO {
    
    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long id;
    
    @NotBlank(message = "帖子标题不能为空")
    @Size(min = 1, max = 200, message = "帖子标题长度为1-200个字符")
    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 13 Pro 二手购买避坑指南（更新版）")
    private String title;
    
    @NotBlank(message = "帖子内容不能为空")
    @Schema(description = "帖子正文", requiredMode = Schema.RequiredMode.REQUIRED, example = "最近在平台淘了一台iPhone 13 Pro...")
    private String content;
    
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long categoryId;
    
    @Schema(description = "标签ID列表", example = "[1, 5, 8, 12]")
    private List<Long> tagIds;
}