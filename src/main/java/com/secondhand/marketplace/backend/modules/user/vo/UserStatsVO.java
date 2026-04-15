package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsVO {
    private Integer productCount;
    private Integer orderCount;
    private Integer favoriteCount;
    private Integer followCount;
    private Integer followerCount;
}