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
@Schema(description = "论坛标签信息")
public class ForumTag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Schema(description = "标签图标URL")
    private String tagIcon;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "是否启用（0-禁用，1-启用）")
    private Integer isEnabled;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}