package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "禁言用户时使用的参数")
public class UserMuteDTO {
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long userId;
    
    @NotNull(message = "禁言天数不能为空")
    @Min(value = 1, message = "禁言天数至少为1天")
    @Schema(description = "禁言天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
    private Integer muteDays;
    
    @NotBlank(message = "禁言原因不能为空")
    @Schema(description = "禁言原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "发布违规内容")
    private String reason;
}