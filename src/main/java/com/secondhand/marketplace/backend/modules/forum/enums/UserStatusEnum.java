package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "系统用户状态类型")
public enum UserStatusEnum {
    
    @Schema(description = "活跃")
    ACTIVE("active", "活跃"),
    
    @Schema(description = "封禁")
    BANNED("banned", "封禁"),
    
    @Schema(description = "待审核")
    PENDING("pending", "待审核");
    
    private final String code;
    private final String desc;
    
    UserStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static UserStatusEnum fromCode(String code) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
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