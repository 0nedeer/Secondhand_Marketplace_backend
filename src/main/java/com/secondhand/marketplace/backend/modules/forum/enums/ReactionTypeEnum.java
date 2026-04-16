package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "用户互动操作类型")
public enum ReactionTypeEnum {
    
    @Schema(description = "点赞")
    LIKE("like", "点赞"),
    
    @Schema(description = "踩")
    DISLIKE("dislike", "踩");
    
    private final String code;
    private final String desc;
    
    ReactionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ReactionTypeEnum fromCode(String code) {
        for (ReactionTypeEnum value : ReactionTypeEnum.values()) {
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