package com.secondhand.marketplace.backend.modules.forum.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "论坛帖子信息")
public class ForumPost implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "帖子ID")
    private Long id;
    
    @Schema(description = "发帖用户ID")
    private Long authorId;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "帖子类型：normal/help/sell/review")
    private String postType;
    
    @Schema(description = "关联商品ID（售卖帖必填）")
    private Long productId;
    
    @Schema(description = "帖子标题")
    private String title;
    
    @Schema(description = "帖子正文")
    private String content;
    
    @Schema(description = "是否删除（0-否，1-是）")
    private int isDeleted;
    
    @Schema(description = "审核状态：pending/approved/rejected")
    private String auditStatus;
    
    @Schema(description = "展示状态：normal/hidden/featured/top")
    private String displayStatus;
    
    @Schema(description = "点赞数")
    private int likeCount;
    
    @Schema(description = "评论数")
    private int commentCount;
    
    @Schema(description = "转发数")
    private int shareCount;
    
    @Schema(description = "收藏数")
    private int collectCount;
    
    @Schema(description = "浏览数")
    private int viewCount;
    
    @Schema(description = "最后评论时间")
    private LocalDateTime lastCommentedAt;
    
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
    
    @Schema(description = "审核驳回原因")
    private String rejectReason;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}