package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "论坛帖子类型")
public enum PostTypeEnum {
    
    @Schema(description = "普通帖子")
    NORMAL("normal", "普通帖子"),
    
    @Schema(description = "求助帖")
    HELP("help", "求助帖"),
    
    @Schema(description = "售卖帖")
    SELL("sell", "售卖帖"),
    
    @Schema(description = "测评帖")
    REVIEW("review", "测评帖");
    
    private final String code;
    private final String desc;
    
    PostTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static PostTypeEnum fromCode(String code) {
        for (PostTypeEnum value : PostTypeEnum.values()) {
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