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
@Schema(description = "论坛分类信息")
public class ForumCategory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "分类ID")
    private Long id;
    
    @Schema(description = "父分类ID（0表示顶级）")
    private Long parentId;
    
    @Schema(description = "分类名称")
    private String name;
    
    @Schema(description = "分类图标URL")
    private String icon;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "是否启用（0-禁用，1-启用）")
    private Integer isEnabled;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}