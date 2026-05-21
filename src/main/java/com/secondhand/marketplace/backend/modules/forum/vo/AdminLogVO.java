package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员操作日志信息")
public class AdminLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "管理员ID")
    private Long adminId;

    @Schema(description = "管理员名称")
    private String adminName;

    @Schema(description = "管理员信息")
    private UserInfoVO adminInfo;

    @Schema(description = "目标类型：user/post/comment/tag")
    private String targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "操作类型")
    private String action;

    @Schema(description = "操作原因")
    private String reason;

    @Schema(description = "操作前数据快照")
    private Object beforeData;

    @Schema(description = "操作后数据快照")
    private Object afterData;

    @Schema(description = "操作IP")
    private String ipAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
