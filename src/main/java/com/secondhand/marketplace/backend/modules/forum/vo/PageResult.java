package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "分页响应结果")
public class PageResult<T> {
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Integer pageNum;
    
    @Schema(description = "每页数量")
    private Integer pageSize;
    
    @Schema(description = "总页数")
    private Integer totalPages;
    
    @Schema(description = "数据列表")
    private List<T> list;
    
    public PageResult(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }
}