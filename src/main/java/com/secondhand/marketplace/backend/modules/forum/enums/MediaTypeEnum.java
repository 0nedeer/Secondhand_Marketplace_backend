package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "帖子媒体附件类型")
public enum MediaTypeEnum {
    
    @Schema(description = "图片")
    IMAGE("image", "图片"),
    
    @Schema(description = "视频")
    VIDEO("video", "视频");
    
    private final String code;
    private final String desc;
    
    MediaTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static MediaTypeEnum fromCode(String code) {
        for (MediaTypeEnum value : MediaTypeEnum.values()) {
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