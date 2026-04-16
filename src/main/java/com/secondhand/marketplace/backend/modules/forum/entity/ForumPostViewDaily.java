package com.secondhand.marketplace.backend.modules.forum.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "论坛帖子日浏览统计信息")
public class ForumPostViewDaily implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "浏览统计ID")
    private Long id;
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "统计日期")
    private LocalDate statDate;
    
    @Schema(description = "独立访客数")
    private Integer uvCount;
    
    @Schema(description = "浏览量")
    private Integer pvCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}