package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "审核内容时使用的参数")
public class AuditReviewDTO {
    
    @NotBlank(message = "目标类型不能为空")
    @Schema(description = "目标类型：post/comment", requiredMode = Schema.RequiredMode.REQUIRED, example = "post")
    private String targetType;
    
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long targetId;
    
    @NotBlank(message = "审核操作不能为空")
    @Schema(description = "审核操作：approve/reject/hide/delete/restore", requiredMode = Schema.RequiredMode.REQUIRED, example = "approve")
    private String action;
    
    @NotBlank(message = "审核原因不能为空")
    @Schema(description = "审核原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "内容合规，予以通过")
    private String reason;
}