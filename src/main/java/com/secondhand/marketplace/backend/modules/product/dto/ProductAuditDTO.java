package com.secondhand.marketplace.backend.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "商品审核请求参数")
public class ProductAuditDTO {
    
    @NotNull(message = "审核结果不能为空")
    @Schema(description = "是否通过 (true=上架, false=驳回)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean approved;

    @Schema(description = "驳回理由 (如果驳回建议填写)")
    private String rejectReason;
}