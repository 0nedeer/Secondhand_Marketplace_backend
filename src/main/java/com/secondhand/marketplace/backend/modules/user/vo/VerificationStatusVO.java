package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationStatusVO {
    private String verifyType;
    private String verifyStatus;
    private String rejectReason;
    private String submittedAt;
}