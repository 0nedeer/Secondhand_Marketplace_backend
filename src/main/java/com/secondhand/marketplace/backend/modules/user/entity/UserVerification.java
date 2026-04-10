package com.secondhand.marketplace.backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_verification")
public class UserVerification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String verifyType;
    private String realName;
    private String idCardNumber;
    private String verifyStatus;

    private LocalDateTime submittedAt;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}