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
@Schema(description = "管理员操作记录")
public class AdminLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "日志ID")
    private Long id;
    
    @Schema(description = "管理员ID")
    private Long adminId;
    
    @Schema(description = "操作目标类型：user/post/comment/tag")
    private String targetType;
    
    @Schema(description = "操作目标ID")
    private Long targetId;
    
    @Schema(description = "操作类型：ban_user/delete_post/approve_post等")
    private String action;
    
    @Schema(description = "操作原因")
    private String reason;
    
    @Schema(description = "操作前数据快照（JSON格式）")
    private String beforeData;
    
    @Schema(description = "操作后数据快照（JSON格式）")
    private String afterData;
    
    @Schema(description = "操作IP")
    private String ipAddress;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}