package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "帖子详细信息")
public class PostVO {
    
    @Schema(description = "帖子ID")
    private Long id;
    
    @Schema(description = "帖子标题")
    private String title;
    
    @Schema(description = "帖子正文")
    private String content;
    
    @Schema(description = "帖子类型：normal/help/sell/review")
    private String postType;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "作者信息")
    private UserInfoVO authorInfo;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "评论数")
    private Integer commentCount;
    
    @Schema(description = "转发数")
    private Integer shareCount;
    
    @Schema(description = "收藏数")
    private Integer collectCount;
    
    @Schema(description = "浏览数")
    private Integer viewCount;
    
    @Schema(description = "标签列表")
    private List<TagVO> tags;
    
    @Schema(description = "媒体附件列表")
    private List<MediaVO> mediaList;
    
    @Schema(description = "审核状态：pending/approved/rejected")
    private String auditStatus;
    
    @Schema(description = "展示状态：normal/hidden/featured/top")
    private String displayStatus;
    
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;
    
    @Schema(description = "当前用户是否已收藏")
    private Boolean isCollected;
    
    @Data
    @Schema(description = "媒体附件信息")
    public static class MediaVO {
        private Long id;
        private String mediaType;
        private String mediaUrl;
        private String coverUrl;
        private Integer sortNo;
    }
}