package com.secondhand.marketplace.backend.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "分页响应结果")
public class PageResult<T> {
    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页数据")
    private List<T> records;

    public PageResult() {}

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
