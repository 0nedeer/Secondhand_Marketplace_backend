package com.secondhand.marketplace.backend.modules.wallet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 自动补齐钱包模块所需表结构，避免数据库未初始化导致运行期 SQL 语法异常。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WalletSchemaAutoInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS wallet_account (
                      id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '钱包账户ID',
                      user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID（卖家）',
                      account_status ENUM('active','frozen','closed') NOT NULL DEFAULT 'active' COMMENT '账户状态',
                      available_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '可用余额（元）',
                      frozen_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额（元）',
                      total_income DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计收入（元）',
                      total_withdraw DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计提现（元）',
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      UNIQUE KEY uk_wallet_account_user (user_id),
                      CONSTRAINT fk_wallet_account_user FOREIGN KEY (user_id) REFERENCES user_account(id),
                      CONSTRAINT chk_wallet_account_balance CHECK (available_balance >= 0 AND frozen_balance >= 0)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包账户表';
                    """);

            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS wallet_ledger (
                      id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '账本流水ID',
                      wallet_account_id BIGINT UNSIGNED NOT NULL COMMENT '钱包账户ID',
                      biz_type ENUM('order_income','refund_out','withdraw_freeze','withdraw_success','withdraw_reject','manual_adjust') NOT NULL COMMENT '业务类型',
                      biz_id BIGINT UNSIGNED NULL COMMENT '业务单据ID（订单/提现等）',
                      change_amount DECIMAL(12,2) NOT NULL COMMENT '变动金额（可正可负）',
                      balance_after DECIMAL(12,2) NOT NULL COMMENT '变动后可用余额',
                      frozen_after DECIMAL(12,2) NOT NULL COMMENT '变动后冻结余额',
                      note VARCHAR(255) NULL COMMENT '备注说明',
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      KEY idx_wallet_ledger_wallet_time (wallet_account_id, created_at),
                      KEY idx_wallet_ledger_biz (biz_type, biz_id),
                      CONSTRAINT fk_wallet_ledger_wallet FOREIGN KEY (wallet_account_id) REFERENCES wallet_account(id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包账本流水表';
                    """);

            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS withdrawal_request (
                      id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '提现申请ID',
                      withdrawal_no VARCHAR(64) NOT NULL COMMENT '提现单号（业务唯一）',
                      user_id BIGINT UNSIGNED NOT NULL COMMENT '申请用户ID（卖家）',
                      wallet_account_id BIGINT UNSIGNED NOT NULL COMMENT '钱包账户ID',
                      amount DECIMAL(10,2) NOT NULL COMMENT '提现金额（元）',
                      fee_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '手续费（元）',
                      channel ENUM('wechat','alipay','bank_card') NOT NULL COMMENT '提现渠道',
                      channel_account_mask VARCHAR(100) NOT NULL COMMENT '提现账号脱敏信息',
                      withdrawal_status ENUM('pending','approved','rejected','processing','paid','failed') NOT NULL DEFAULT 'pending' COMMENT '提现状态',
                      reviewed_by BIGINT UNSIGNED NULL COMMENT '审核管理员ID',
                      reviewed_at DATETIME NULL COMMENT '审核时间',
                      paid_at DATETIME NULL COMMENT '打款时间',
                      reject_reason VARCHAR(255) NULL COMMENT '驳回原因',
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      UNIQUE KEY uk_withdrawal_request_no (withdrawal_no),
                      KEY idx_withdrawal_request_user (user_id, created_at),
                      KEY idx_withdrawal_request_status (withdrawal_status),
                      CONSTRAINT fk_withdrawal_request_user FOREIGN KEY (user_id) REFERENCES user_account(id),
                      CONSTRAINT fk_withdrawal_request_wallet FOREIGN KEY (wallet_account_id) REFERENCES wallet_account(id),
                      CONSTRAINT fk_withdrawal_request_admin FOREIGN KEY (reviewed_by) REFERENCES user_account(id),
                      CONSTRAINT chk_withdrawal_request_amount CHECK (amount > 0 AND fee_amount >= 0)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卖家提现申请表';
                    """);

            log.info("Wallet schema check done: wallet tables are ready.");
        } catch (Exception e) {
            log.error("Wallet schema auto initialization failed. Please check DB permission and DDL.", e);
        }
    }
}

