package com.secondhand.marketplace.backend.modules.forum.enums;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "举报处理状态类型")
public enum ReportStatusEnum {
    
    @Schema(description = "待处理")
    PENDING("pending", "待处理"),
    
    @Schema(description = "处理中")
    PROCESSING("processing", "处理中"),
    
    @Schema(description = "已解决")
    RESOLVED("resolved", "已解决"),
    
    @Schema(description = "已驳回")
    REJECTED("rejected", "已驳回");
    
    private final String code;
    private final String desc;
    
    ReportStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ReportStatusEnum fromCode(String code) {
        for (ReportStatusEnum value : ReportStatusEnum.values()) {
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