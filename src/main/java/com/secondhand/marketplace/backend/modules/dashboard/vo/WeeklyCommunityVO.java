package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;
import java.util.List;

@Data
public class WeeklyCommunityVO {
    private List<Long> posts;
    private List<Long> comments;
}
