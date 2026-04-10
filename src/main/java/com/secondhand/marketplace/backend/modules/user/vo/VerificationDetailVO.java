package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationDetailVO {
    private Long id;
    private String verifyType;
    private String realName;
    private String idCardNumber;  // 脱敏展示
    private String verifyStatus;
    private String rejectReason;
    private String submittedAt;
    private String reviewedAt;
}