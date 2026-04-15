package com.secondhand.marketplace.backend.modules.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("after_sale_evidence")
public class AfterSaleEvidence {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long afterSaleId;
    private String evidenceType;
    private String contentUrl;
    private String contentText;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}
