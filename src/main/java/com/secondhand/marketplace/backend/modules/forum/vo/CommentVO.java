package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "评论详细信息")
public class CommentVO {
    
    @Schema(description = "评论ID")
    private Long id;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "评论者信息")
    private UserInfoVO commenterInfo;
    
    @Schema(description = "被回复者信息")
    private UserInfoVO replyToUserInfo;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "回复数")
    private Integer replyCount;
    
    @Schema(description = "回复列表")
    private List<CommentVO> replyList;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;
}