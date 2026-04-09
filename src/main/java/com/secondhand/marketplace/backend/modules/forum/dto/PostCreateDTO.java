package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "发布新帖子时使用的参数")
public class PostCreateDTO {
    
    @NotBlank(message = "帖子标题不能为空")
    @Size(min = 1, max = 200, message = "帖子标题长度为1-200个字符")
    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 13 Pro 二手购买避坑指南")
    private String title;
    
    @NotBlank(message = "帖子内容不能为空")
    @Schema(description = "帖子正文", requiredMode = Schema.RequiredMode.REQUIRED, example = "最近在平台淘了一台iPhone 13 Pro...")
    private String content;
    
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long categoryId;
    
    @NotBlank(message = "帖子类型不能为空")
    @Schema(description = "帖子类型：normal/help/sell/review", requiredMode = Schema.RequiredMode.REQUIRED, example = "review")
    private String postType;
    
    @Schema(description = "标签ID列表", example = "[1, 5, 8]")
    private List<Long> tagIds;
    
    @Schema(description = "关联商品ID（售卖帖必填）", example = "10001")
    private Long productId;
}