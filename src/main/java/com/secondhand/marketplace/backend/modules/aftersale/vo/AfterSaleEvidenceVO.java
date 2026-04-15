package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AfterSaleEvidenceVO {
    private Long id;
    private Long afterSaleId;
    private String evidenceType;
    private String contentUrl;
    private String contentText;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}
