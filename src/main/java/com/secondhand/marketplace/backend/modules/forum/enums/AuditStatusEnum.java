package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "内容审核状态")
public enum AuditStatusEnum {
    
    @Schema(description = "待审核")
    PENDING("pending", "待审核"),
    
    @Schema(description = "已通过")
    APPROVED("approved", "已通过"),
    
    @Schema(description = "已驳回")
    REJECTED("rejected", "已驳回");
    
    private final String code;
    private final String desc;
    
    AuditStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AuditStatusEnum fromCode(String code) {
        for (AuditStatusEnum value : AuditStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * 校验code是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}