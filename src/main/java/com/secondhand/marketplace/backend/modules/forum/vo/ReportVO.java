package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "举报详细信息")
public class ReportVO {
    
    @Schema(description = "举报ID")
    private Long id;
    
    @Schema(description = "目标类型：post/comment")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "目标标题")
    private String targetTitle;
    
    @Schema(description = "举报人信息")
    private UserInfoVO reporterInfo;
    
    @Schema(description = "举报原因")
    private String reportReason;
    
    @Schema(description = "举报详情")
    private String reportDetail;
    
    @Schema(description = "证据图片URL列表（JSON格式）")
    private String evidenceUrls;
    
    @Schema(description = "举报状态：pending/processing/resolved/rejected")
    private String reportStatus;
    
    @Schema(description = "处理结果")
    private String handleResult;
    
    @Schema(description = "处理人信息")
    private UserInfoVO handlerInfo;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "处理时间")
    private LocalDateTime handledAt;
}