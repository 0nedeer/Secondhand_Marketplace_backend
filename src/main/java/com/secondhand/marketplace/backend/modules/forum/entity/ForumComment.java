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
@Schema(description = "论坛评论信息")
public class ForumComment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "评论ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "父评论ID（0表示顶级评论）")
    private Long parentCommentId;
    
    @Schema(description = "回复的目标用户ID")
    private Long replyToUserId;
    
    @Schema(description = "评论用户ID")
    private Long commenterId;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "是否删除（0-否，1-是）")
    private Integer isDeleted;
    
    @Schema(description = "审核状态：pending/approved/rejected")
    private String auditStatus;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "回复数（仅顶级评论使用）")
    private Integer replyCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}