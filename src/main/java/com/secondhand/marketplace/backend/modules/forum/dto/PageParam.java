package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Schema(description = "分页请求参数")
public class PageParam {
    
    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}