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
@Schema(description = "论坛审核操作记录")
public class ForumAuditLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "日志ID")
    private Long id;
    
    @Schema(description = "审核目标类型：post/comment")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "审核员ID")
    private Long auditorId;
    
    @Schema(description = "审核操作：approve/reject/hide/delete/restore")
    private String action;
    
    @Schema(description = "审核原因/驳回原因")
    private String reason;
    
    @Schema(description = "旧状态")
    private String oldStatus;
    
    @Schema(description = "新状态")
    private String newStatus;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}