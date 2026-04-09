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
@Schema(description = "帖子与标签的关联信息")
public class ForumPostTag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "关联ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "标签ID")
    private Long tagId;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}