package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "帖子每日浏览统计数据")
public class PostDailyStatsVO {
    
    @Schema(description = "帖子ID")
    private Long postId;
    
    @Schema(description = "统计日期")
    private LocalDate statDate;
    
    @Schema(description = "独立访客数")
    private Integer uvCount;
    
    @Schema(description = "页面浏览量")
    private Integer pvCount;
    
    @Schema(description = "帖子标题")
    private String postTitle;
    
    @Schema(description = "作者名称")
    private String authorName;
}