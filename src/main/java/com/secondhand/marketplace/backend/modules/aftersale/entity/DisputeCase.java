package com.secondhand.marketplace.backend.modules.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dispute_case")
public class DisputeCase {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String disputeNo;
    private Long orderId;
    private Long afterSaleId;
    private Long buyerId;
    private Long sellerId;
    private String currentStatus;
    private String responsibility;
    private String resolutionResult;
    private BigDecimal resolutionAmount;
    private Long resolvedBy;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
