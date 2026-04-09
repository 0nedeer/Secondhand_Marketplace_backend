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
@Schema(description = "论坛帖子转发信息")
public class ForumPostShare implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "转发记录ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "转发用户ID")
    private Long userId;
    
    @Schema(description = "转发渠道：in_app/wechat/qq/weibo/copy_link")
    private String shareChannel;
    
    @Schema(description = "转发时间")
    private LocalDateTime createdAt;
}