package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "管理员操作目标类型")
public enum AdminLogTargetTypeEnum {
    
    @Schema(description = "用户")
    USER("user", "用户"),
    
    @Schema(description = "帖子")
    POST("post", "帖子"),
    
    @Schema(description = "评论")
    COMMENT("comment", "评论"),
    
    @Schema(description = "标签")
    TAG("tag", "标签");
    
    private final String code;
    private final String desc;
    
    AdminLogTargetTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AdminLogTargetTypeEnum fromCode(String code) {
        for (AdminLogTargetTypeEnum value : AdminLogTargetTypeEnum.values()) {
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