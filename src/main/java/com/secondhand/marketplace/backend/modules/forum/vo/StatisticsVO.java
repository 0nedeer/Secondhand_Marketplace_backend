package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "论坛整体统计数据")
public class StatisticsVO {
    
    @Schema(description = "总帖子数")
    private Long totalPostCount;
    
    @Schema(description = "总评论数")
    private Long totalCommentCount;
    
    @Schema(description = "今日帖子数")
    private Integer todayPostCount;
    
    @Schema(description = "今日评论数")
    private Integer todayCommentCount;
    
    @Schema(description = "总用户数")
    private Long totalUserCount;
    
    @Schema(description = "今日注册用户数")
    private Integer todayUserCount;
    
    @Schema(description = "总标签数")
    private Integer totalTagCount;
    
    @Schema(description = "总分类数")
    private Integer totalCategoryCount;
}