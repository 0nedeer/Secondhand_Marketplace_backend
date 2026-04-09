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
@Schema(description = "论坛点赞/踩等互动信息")
public class ForumReaction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "互动ID")
    private Long id;
    
    @Schema(description = "互动目标类型：post/comment")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "操作用户ID")
    private Long userId;
    
    @Schema(description = "互动类型：like/dislike")
    private String reactionType;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}