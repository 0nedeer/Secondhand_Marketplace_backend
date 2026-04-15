package com.secondhand.marketplace.backend.modules.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateLogisticsTraceRequest {

    private LocalDateTime traceTime;

    @NotBlank(message = "轨迹状态不能为空")
    @Size(max = 100, message = "轨迹状态长度不能超过100")
    private String traceStatus;

    @NotBlank(message = "轨迹详情不能为空")
    @Size(max = 500, message = "轨迹详情长度不能超过500")
    private String traceDetail;

    @Size(max = 255, message = "轨迹地点长度不能超过255")
    private String traceLocation;
}
