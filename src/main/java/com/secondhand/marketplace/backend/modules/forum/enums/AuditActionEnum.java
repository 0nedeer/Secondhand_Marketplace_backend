package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "内容审核操作类型")
public enum AuditActionEnum {
    
    @Schema(description = "通过")
    APPROVE("approve", "通过"),
    
    @Schema(description = "驳回")
    REJECT("reject", "驳回"),
    
    @Schema(description = "隐藏")
    HIDE("hide", "隐藏"),
    
    @Schema(description = "删除")
    DELETE("delete", "删除"),
    
    @Schema(description = "恢复")
    RESTORE("restore", "恢复");
    
    private final String code;
    private final String desc;
    
    AuditActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AuditActionEnum fromCode(String code) {
        for (AuditActionEnum value : AuditActionEnum.values()) {
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