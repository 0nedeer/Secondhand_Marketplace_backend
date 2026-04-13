package com.secondhand.marketplace.backend.modules.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_status_log")
public class OrderStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;
    private String fromStatus;
    private String toStatus;
    private Long changedBy;
    private String changeReason;
    private LocalDateTime changedAt;
}

