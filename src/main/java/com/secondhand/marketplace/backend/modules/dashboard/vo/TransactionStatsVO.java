package com.secondhand.marketplace.backend.modules.dashboard.vo;

import lombok.Data;

@Data
public class TransactionStatsVO {
    private long totalTransactions;
    private double transactionAmount;
    private double transactionSuccessRate;
    private double disputeRate;
}
