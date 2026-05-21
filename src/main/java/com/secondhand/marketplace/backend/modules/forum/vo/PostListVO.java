package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "帖子列表中的简化信息")
public class PostListVO {
    
    @Schema(description = "帖子ID")
    private Long id;
    
    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容摘要")
    private String content;
    
    @Schema(description = "帖子类型：normal/help/sell/review")
    private String postType;

    @Schema(description = "作者ID")
    private Long authorId;
    
    @Schema(description = "作者名称")
    private String authorName;
    
    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "审核状态：pending/approved/rejected")
    private String status;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否精华")
    private Boolean isFeatured;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "评论数")
    private Integer commentCount;
    
    @Schema(description = "浏览数")
    private Integer viewCount;
    
    @Schema(description = "第一张媒体图片URL")
    private String firstMediaUrl;
    
    @Schema(description = "标签名称列表")
    private List<String> tagNames;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
