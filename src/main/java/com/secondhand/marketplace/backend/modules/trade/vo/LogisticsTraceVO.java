package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticsTraceVO {
    private Long id;
    private Long shipmentId;
    private LocalDateTime traceTime;
    private String traceStatus;
    private String traceDetail;
    private String traceLocation;
    private LocalDateTime createdAt;
}
