package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "帖子展示状态")
public enum DisplayStatusEnum {
    
    @Schema(description = "正常")
    NORMAL("normal", "正常"),
    
    @Schema(description = "隐藏")
    HIDDEN("hidden", "隐藏"),
    
    @Schema(description = "精华")
    FEATURED("featured", "精华"),
    
    @Schema(description = "置顶")
    TOP("top", "置顶");
    
    private final String code;
    private final String desc;
    
    DisplayStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static DisplayStatusEnum fromCode(String code) {
        for (DisplayStatusEnum value : DisplayStatusEnum.values()) {
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