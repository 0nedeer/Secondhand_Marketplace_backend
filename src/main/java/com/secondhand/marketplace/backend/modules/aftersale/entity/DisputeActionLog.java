package com.secondhand.marketplace.backend.modules.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dispute_action_log")
public class DisputeActionLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long disputeId;
    private Long actionBy;
    private String actionType;
    private String actionDesc;
    private LocalDateTime createdAt;
}
