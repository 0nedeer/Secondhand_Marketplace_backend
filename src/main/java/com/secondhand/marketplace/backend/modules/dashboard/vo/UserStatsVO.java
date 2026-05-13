package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class UserStatsVO {
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long newUsersToday;
}
