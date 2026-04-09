package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "搜索帖子时使用的参数")
public class PostSearchDTO extends PageParam {
    
    @Schema(description = "分类ID", example = "2")
    private Long categoryId;
    
    @Schema(description = "帖子类型：normal/help/sell/review", example = "review")
    private String postType;
    
    @Schema(description = "关键词（标题/内容）", example = "iPhone 13")
    private String keyword;
    
    @Schema(description = "排序字段：created_at, published_at, like_count, view_count, comment_count", example = "created_at")
    private String sortBy;
    
    @Schema(description = "排序方向：ASC, DESC", example = "DESC")
    private String order;
    
    @Schema(description = "显示状态：normal, top, featured", example = "normal")
    private String displayStatus;
}