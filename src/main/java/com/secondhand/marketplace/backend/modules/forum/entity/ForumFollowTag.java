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
@Schema(description = "用户关注标签信息")
public class ForumFollowTag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "关注ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "标签ID")
    private Long tagId;
    
    @Schema(description = "关注时间")
    private LocalDateTime createdAt;
}