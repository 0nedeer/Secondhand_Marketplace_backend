package com.secondhand.marketplace.backend.modules.trade.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeProduct {
    private Long id;
    private Long sellerId;
    private String title;
    private BigDecimal sellingPrice;
    private Integer stock;
    private String publishStatus;
}

