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
@Schema(description = "论坛帖子收藏信息")
public class ForumCollect implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "收藏ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "收藏时间")
    private LocalDateTime createdAt;
}