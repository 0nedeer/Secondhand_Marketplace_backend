package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户收藏帖子信息")
public class CollectVO {
    
    @Schema(description = "收藏ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "帖子标题")
    private String postTitle;
    
    @Schema(description = "帖子作者")
    private String postAuthor;
    
    @Schema(description = "收藏时间")
    private LocalDateTime collectedAt;
    
    @Schema(description = "帖子创建时间")
    private LocalDateTime postCreatedAt;
    
    @Schema(description = "帖子浏览数")
    private Integer postViewCount;
    
    @Schema(description = "帖子评论数")
    private Integer postCommentCount;
}