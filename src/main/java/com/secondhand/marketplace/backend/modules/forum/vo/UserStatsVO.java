package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户相关统计数据")
public class UserStatsVO {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "帖子数")
    private Integer postCount;
    
    @Schema(description = "评论数")
    private Integer commentCount;
    
    @Schema(description = "获得的点赞数")
    private Integer likeReceivedCount;
    
    @Schema(description = "粉丝数")
    private Integer followerCount;
    
    @Schema(description = "关注数")
    private Integer followingCount;
    
    @Schema(description = "收藏数")
    private Integer collectCount;
    
    @Schema(description = "信用分")
    private Integer creditScore;
}