package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "审核操作日志信息")
public class AuditLogVO {
    
    @Schema(description = "日志ID")
    private Long id;
    
    @Schema(description = "目标类型：post/comment")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "审核员信息")
    private UserInfoVO auditorInfo;
    
    @Schema(description = "操作：approve/reject/hide/delete/restore")
    private String action;
    
    @Schema(description = "操作原因")
    private String reason;
    
    @Schema(description = "旧状态")
    private String oldStatus;
    
    @Schema(description = "新状态")
    private String newStatus;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}