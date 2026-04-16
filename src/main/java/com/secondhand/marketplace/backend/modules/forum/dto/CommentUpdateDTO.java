package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "更新评论时使用的参数")
public class CommentUpdateDTO {
    
    @NotNull(message = "评论ID不能为空")
    @Schema(description = "评论ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    private Long id;
    
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "非常有用的分享，谢谢！（更新）")
    private String content;
}