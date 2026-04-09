package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "论坛标签信息")
public class TagVO {
    
    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Schema(description = "标签图标")
    private String tagIcon;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "是否被当前用户关注")
    private Boolean isFollowed;
}