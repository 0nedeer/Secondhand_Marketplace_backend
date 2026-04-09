package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "论坛分类信息")
public class CategoryVO {
    
    @Schema(description = "分类ID")
    private Long id;
    
    @Schema(description = "分类名称")
    private String name;
    
    @Schema(description = "分类图标")
    private String icon;
    
    @Schema(description = "父分类ID")
    private Long parentId;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
    
    public CategoryVO() {
        this.children = new ArrayList<>();
    }
}