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
    
    @Schema(description = "帖子类型：normal/help/sell/review")
    private String postType;
    
    @Schema(description = "作者名称")
    private String authorName;
    
    @Schema(description = "作者头像")
    private String authorAvatar;
    
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
}