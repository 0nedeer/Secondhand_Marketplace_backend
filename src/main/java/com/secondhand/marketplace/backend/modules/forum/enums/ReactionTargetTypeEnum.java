package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "互动操作的目标类型")
public enum ReactionTargetTypeEnum {
    
    @Schema(description = "帖子")
    POST("post", "帖子"),
    
    @Schema(description = "评论")
    COMMENT("comment", "评论");
    
    private final String code;
    private final String desc;
    
    ReactionTargetTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ReactionTargetTypeEnum fromCode(String code) {
        for (ReactionTargetTypeEnum value : ReactionTargetTypeEnum.values()) {
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