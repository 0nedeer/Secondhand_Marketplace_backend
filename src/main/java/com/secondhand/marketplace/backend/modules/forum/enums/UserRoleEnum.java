package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "系统用户角色类型")
public enum UserRoleEnum {
    
    @Schema(description = "普通用户")
    USER("user", "普通用户"),
    
    @Schema(description = "管理员")
    ADMIN("admin", "管理员"),
    
    @Schema(description = "超级管理员")
    SUPER_ADMIN("super_admin", "超级管理员");
    
    private final String code;
    private final String desc;
    
    UserRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static UserRoleEnum fromCode(String code) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
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