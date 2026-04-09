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
@Schema(description = "帖子的媒体附件信息")
public class ForumPostMedia implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "媒体ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "媒体类型：image/video")
    private String mediaType;
    
    @Schema(description = "媒体URL")
    private String mediaUrl;
    
    @Schema(description = "视频封面URL")
    private String coverUrl;
    
    @Schema(description = "排序序号")
    private Integer sortNo;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}