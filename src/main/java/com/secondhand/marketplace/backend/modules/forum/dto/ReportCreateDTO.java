package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "举报内容时使用的参数")
public class ReportCreateDTO {
    
    @NotBlank(message = "目标类型不能为空")
    @Schema(description = "目标类型：post/comment", requiredMode = Schema.RequiredMode.REQUIRED, example = "post")
    private String targetType;
    
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long targetId;
    
    @NotBlank(message = "举报原因不能为空")
    @Schema(description = "举报原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "违规内容")
    private String reportReason;
    
    @NotBlank(message = "举报详情不能为空")
    @Schema(description = "举报详细描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "该帖子包含违规信息")
    private String reportDetail;
    
    @Schema(description = "证据图片URL列表（JSON格式）", example = "[\"https://example.com/evidence1.jpg\", \"https://example.com/evidence2.jpg\"]")
    private String evidenceUrls;
}