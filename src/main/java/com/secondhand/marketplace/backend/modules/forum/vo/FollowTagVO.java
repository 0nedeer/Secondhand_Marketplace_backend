package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户关注的标签信息")
public class FollowTagVO {
    
    @Schema(description = "关注ID")
    private Long id;
    
    @Schema(description = "标签ID")
    private Long tagId;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Schema(description = "标签图标")
    private String tagIcon;
    
    @Schema(description = "关注时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "标签下帖子数量")
    private Integer postCount;
}