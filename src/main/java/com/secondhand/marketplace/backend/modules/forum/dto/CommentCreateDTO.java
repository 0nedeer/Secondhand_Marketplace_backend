package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "发布评论时使用的参数")
public class CommentCreateDTO {
    
    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long postId;
    
    @Schema(description = "父评论ID（0表示顶级评论）", example = "0")
    private Long parentCommentId;
    
    @Schema(description = "回复的目标用户ID", example = "100")
    private Long replyToUserId;
    
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "非常有用的分享，谢谢！")
    private String content;
}