package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "帖子转发渠道类型")
public enum ShareChannelEnum {
    
    @Schema(description = "站内转发")
    IN_APP("in_app", "站内转发"),
    
    @Schema(description = "微信")
    WECHAT("wechat", "微信"),
    
    @Schema(description = "QQ")
    QQ("qq", "QQ"),
    
    @Schema(description = "微博")
    WEIBO("weibo", "微博"),
    
    @Schema(description = "复制链接")
    COPY_LINK("copy_link", "复制链接");
    
    private final String code;
    private final String desc;
    
    ShareChannelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ShareChannelEnum fromCode(String code) {
        for (ShareChannelEnum value : ShareChannelEnum.values()) {
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