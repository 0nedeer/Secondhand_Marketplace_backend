package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "用户实名认证状态类型")
public enum RealNameStatusEnum {
    
    @Schema(description = "未认证")
    UNVERIFIED("unverified", "未认证"),
    
    @Schema(description = "认证中")
    PENDING("pending", "认证中"),
    
    @Schema(description = "已认证")
    VERIFIED("verified", "已认证"),
    
    @Schema(description = "认证失败")
    FAILED("failed", "认证失败");
    
    private final String code;
    private final String desc;
    
    RealNameStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static RealNameStatusEnum fromCode(String code) {
        for (RealNameStatusEnum value : RealNameStatusEnum.values()) {
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