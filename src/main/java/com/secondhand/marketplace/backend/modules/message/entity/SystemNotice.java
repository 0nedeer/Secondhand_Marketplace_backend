package com.secondhand.marketplace.backend.modules.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_notice")
public class SystemNotice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String noticeType;

    private String title;

    private String content;

    private String targetScope;

    private String targetRoleCode;

    private Long targetUserId;

    private String publishStatus;

    private LocalDateTime publishedAt;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}