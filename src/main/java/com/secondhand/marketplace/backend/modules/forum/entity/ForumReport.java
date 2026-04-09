package com.secondhand.marketplace.backend.modules.forum.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "论坛举报信息")
public class ForumReport implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "举报ID")
    private Long id;
    
    @Schema(description = "举报目标类型：post/comment")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "举报人ID")
    private Long reporterId;
    
    @Schema(description = "举报原因（违规内容/广告/欺诈等）")
    private String reportReason;
    
    @Schema(description = "举报详细描述")
    private String reportDetail;
    
    @Schema(description = "证据图片URL列表（JSON格式）")
    private String evidenceUrls;
    
    @Schema(description = "处理状态：pending/processing/resolved/rejected")
    private String reportStatus;
    
    @Schema(description = "处理管理员ID")
    private Long handledBy;
    
    @Schema(description = "处理结果说明")
    private String handleResult;
    
    @Schema(description = "处理时间")
    private LocalDateTime handledAt;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}