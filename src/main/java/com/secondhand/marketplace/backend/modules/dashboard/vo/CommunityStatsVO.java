package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class CommunityStatsVO {
    private long totalForumPosts;
    private long approvedForumPosts;
    private long pendingForumPosts;
    private long forumPostViews;
    private long totalComments;
    private long totalLikes;
}
