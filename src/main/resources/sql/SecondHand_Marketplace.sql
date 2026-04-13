-- 二手交易平台数据库设计（MySQL 8.0+）
-- 说明：覆盖买家、卖家、管理员三类角色全流程（商品、聊天、订单、支付、物流、评价、售后、论坛、AI问答、平台治理）
-- 身份模型：用户默认可同时作为买家/卖家；管理员通过 user_account.is_admin 标识

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS secondhand_marketplace
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE secondhand_marketplace;

-- =========================
-- 0) 反向删除旧表（便于重建）
-- =========================
DROP TABLE IF EXISTS platform_daily_metric;
DROP TABLE IF EXISTS user_notice_inbox;
DROP TABLE IF EXISTS system_notice;
DROP TABLE IF EXISTS report_ticket;
DROP TABLE IF EXISTS content_moderation_record;
DROP TABLE IF EXISTS admin_action_log;
DROP TABLE IF EXISTS ai_qa_message;
DROP TABLE IF EXISTS ai_qa_session;
DROP TABLE IF EXISTS forum_post_view_daily;
DROP TABLE IF EXISTS forum_post_share;
DROP TABLE IF EXISTS forum_reaction;
DROP TABLE IF EXISTS forum_comment;
DROP TABLE IF EXISTS forum_post_media;
DROP TABLE IF EXISTS forum_post_tag;
DROP TABLE IF EXISTS forum_post;
DROP TABLE IF EXISTS forum_tag;
DROP TABLE IF EXISTS dispute_action_log;
DROP TABLE IF EXISTS dispute_case;
DROP TABLE IF EXISTS after_sale_evidence;
DROP TABLE IF EXISTS after_sale_request;
DROP TABLE IF EXISTS review_image;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS order_status_log;
DROP TABLE IF EXISTS logistics_trace;
DROP TABLE IF EXISTS order_shipment;
DROP TABLE IF EXISTS escrow_record;
DROP TABLE IF EXISTS payment_transaction;
DROP TABLE IF EXISTS payment_order;
DROP TABLE IF EXISTS wallet_ledger;
DROP TABLE IF EXISTS wallet_account;
DROP TABLE IF EXISTS withdrawal_request;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS trade_order;
DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS conversation_member;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS seller_follow;
DROP TABLE IF EXISTS product_favorite;
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS seller_reputation_snapshot;
DROP TABLE IF EXISTS user_verification;
DROP TABLE IF EXISTS user_address;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS `user`;



CREATE TABLE `user_account` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（平台唯一）',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号，唯一约束，可空',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱，唯一约束，可空',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    `can_buy` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否具备买家能力，默认1，约束0/1',
    `can_sell` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否具备卖家能力，默认1，约束0/1',
    `is_admin` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否管理员，默认0，约束0/1',
    `user_status` ENUM('pending', 'active', 'banned') NOT NULL DEFAULT 'pending' COMMENT '用户状态',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间，可空',
    `registered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间，默认当前时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    CONSTRAINT `chk_can_buy` CHECK (`can_buy` IN (0, 1)),
    CONSTRAINT `chk_can_sell` CHECK (`can_sell` IN (0, 1)),
    CONSTRAINT `chk_is_admin` CHECK (`is_admin` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础账户表';


CREATE TABLE `user_profile` (
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，主键，外键 user_account.id',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '用户头像URL，可空',
    `gender` ENUM('unknown', 'male', 'female') NOT NULL DEFAULT 'unknown' COMMENT '性别',
    `birthday` DATE DEFAULT NULL COMMENT '生日，可空',
    `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介，可空',
    `city` VARCHAR(100) DEFAULT NULL COMMENT '所在城市，可空',
    `district` VARCHAR(100) DEFAULT NULL COMMENT '所在区县，可空',
    `credit_score` INT NOT NULL DEFAULT 100 COMMENT '平台信用分，默认100，最小值0',
    `positive_rate` DECIMAL(5,2) NOT NULL DEFAULT 100.00 COMMENT '好评率，默认100.00，范围0~100',
    `total_review_count` INT NOT NULL DEFAULT 0 COMMENT '累计评价数，默认0',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_user_profile_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `chk_credit_score` CHECK (`credit_score` >= 0),
    CONSTRAINT `chk_positive_rate` CHECK (`positive_rate` >= 0 AND `positive_rate` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展资料与信誉表';


CREATE TABLE `user_address` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键，自增',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，外键 user_account.id',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址，0-否，1-是，默认0',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_user_address_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `chk_is_default` CHECK (`is_default` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';


CREATE TABLE `user_verification` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '认证记录ID，主键，自增',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID，外键 user_account.id',
    `verify_type` ENUM('real_name', 'student', 'merchant') NOT NULL COMMENT '认证类型',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名（仅实名认证时填写），可空',
    `id_card_number` VARCHAR(18) DEFAULT NULL COMMENT '身份证号码（加密存储，仅实名认证时填写），可空',
    `verify_status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '认证状态',
    `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间，默认当前时间',
    `reviewed_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '审核管理员ID，外键 user_account.id，可空',
    `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间，可空',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因，可空',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_verify_status` (`verify_status`),
    CONSTRAINT `fk_user_verification_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_user_verification_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `user_account` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证审核表';


CREATE TABLE `seller_reputation_snapshot` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快照ID，主键，自增',
    `seller_id` BIGINT UNSIGNED NOT NULL COMMENT '卖家ID，外键 user_account.id',
    `snapshot_date` DATE NOT NULL COMMENT '快照日期，与 seller_id 组成唯一约束',
    `credit_score` INT NOT NULL COMMENT '当日信用分',
    `positive_rate` DECIMAL(5,2) NOT NULL COMMENT '当日好评率',
    `total_orders` INT NOT NULL DEFAULT 0 COMMENT '累计订单数，默认0',
    `completed_orders` INT NOT NULL DEFAULT 0 COMMENT '完成订单数，默认0',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_seller_date` (`seller_id`, `snapshot_date`),
    CONSTRAINT `fk_seller_reputation_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `chk_credit_score_snapshot` CHECK (`credit_score` >= 0),
    CONSTRAINT `chk_positive_rate_snapshot` CHECK (`positive_rate` >= 0 AND `positive_rate` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卖家信誉日快照表';


-- ======================================================
-- 第一部分：基础用户表（论坛必需）
-- ======================================================

-- 1. 用户主数据统一使用 user_account + user_profile（不再单独维护 user 表）

-- 2. 管理员操作日志表
DROP TABLE IF EXISTS `admin_log`;
CREATE TABLE `admin_log` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `admin_id` BIGINT UNSIGNED NOT NULL COMMENT '管理员ID',
    `target_type` ENUM('user', 'post', 'comment', 'tag') DEFAULT NULL COMMENT '操作目标类型',
    `target_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作目标ID',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型（ban_user, delete_post, approve_post等）',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '操作原因',
    `before_data` JSON DEFAULT NULL COMMENT '操作前数据快照',
    `after_data` JSON DEFAULT NULL COMMENT '操作后数据快照',
    `ip_address` VARCHAR(45) DEFAULT NULL COMMENT '操作IP',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_admin_id` (`admin_id`),
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- ======================================================
-- 第二部分：论坛核心表
-- ======================================================

-- 3. 论坛分类表
DROP TABLE IF EXISTS `forum_category`;
CREATE TABLE `forum_category` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用，0-禁用，1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛分类表';

-- 4. 论坛标签表
DROP TABLE IF EXISTS `forum_tag`;
CREATE TABLE `forum_tag` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `tag_icon` VARCHAR(255) DEFAULT NULL COMMENT '标签图标URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用，0-禁用，1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`),
    INDEX `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛标签表';

-- 5. 论坛帖子表
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    `author_id` BIGINT UNSIGNED NOT NULL COMMENT '发帖用户ID',
    `category_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '分类ID',
    `post_type` ENUM('normal', 'help', 'sell', 'review') NOT NULL DEFAULT 'normal' COMMENT '帖子类型',
    `product_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联商品ID（售卖帖必填）',
    `title` VARCHAR(200) NOT NULL COMMENT '帖子标题',
    `content` MEDIUMTEXT NOT NULL COMMENT '帖子正文',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    `audit_status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '审核状态',
    `display_status` ENUM('normal', 'hidden', 'featured', 'top') NOT NULL DEFAULT 'normal' COMMENT '展示状态',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '转发数',
    `collect_count` INT NOT NULL DEFAULT 0 COMMENT '收藏数',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `last_commented_at` DATETIME DEFAULT NULL COMMENT '最后评论时间',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '审核驳回原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_audit_status` (`audit_status`),
    INDEX `idx_display_status` (`display_status`),
    INDEX `idx_published_at` (`published_at`),
    INDEX `idx_last_commented_at` (`last_commented_at`),
    INDEX `idx_is_deleted` (`is_deleted`),
    FULLTEXT INDEX `idx_ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子表';

-- 6. 帖子与标签关联表
DROP TABLE IF EXISTS `forum_post_tag`;
CREATE TABLE `forum_post_tag` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子标签关联表';

-- 7. 帖子媒体附件表
DROP TABLE IF EXISTS `forum_post_media`;
CREATE TABLE `forum_post_media` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '媒体ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `media_type` ENUM('image', 'video') NOT NULL COMMENT '媒体类型',
    `media_url` VARCHAR(500) NOT NULL COMMENT '媒体URL',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '视频封面URL',
    `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子媒体附件表';

-- 8. 论坛评论表
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `parent_comment_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    `reply_to_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '回复的目标用户ID',
    `commenter_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
    `content` VARCHAR(2000) NOT NULL COMMENT '评论内容',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    `audit_status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '审核状态',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数（仅顶级评论使用）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_parent_comment_id` (`parent_comment_id`),
    INDEX `idx_commenter_id` (`commenter_id`),
    INDEX `idx_audit_status` (`audit_status`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛评论表';

-- 9. 论坛互动表（点赞/踩）
DROP TABLE IF EXISTS `forum_reaction`;
CREATE TABLE `forum_reaction` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '互动ID',
    `target_type` ENUM('post', 'comment') NOT NULL COMMENT '互动目标类型',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '操作用户ID',
    `reaction_type` ENUM('like', 'dislike') NOT NULL DEFAULT 'like' COMMENT '互动类型',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_target_user` (`target_type`, `target_id`, `user_id`),
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛互动表';

-- 10. 论坛收藏数
DROP TABLE IF EXISTS `forum_collect`;
CREATE TABLE `forum_collect` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛收藏表';

-- 11. 论坛帖子转发数
DROP TABLE IF EXISTS `forum_post_share`;
CREATE TABLE `forum_post_share` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '转发记录ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '转发用户ID',
    `share_channel` ENUM('in_app', 'wechat', 'qq', 'weibo', 'copy_link') NOT NULL COMMENT '转发渠道',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '转发时间',
    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子转发表';

-- 12. 论坛举报表
DROP TABLE IF EXISTS `forum_report`;
CREATE TABLE `forum_report` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `target_type` ENUM('post', 'comment') NOT NULL COMMENT '举报目标类型',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
    `reporter_id` BIGINT UNSIGNED NOT NULL COMMENT '举报人ID',
    `report_reason` VARCHAR(100) NOT NULL COMMENT '举报原因（违规内容/广告/欺诈等）',
    `report_detail` VARCHAR(500) DEFAULT NULL COMMENT '举报详细描述',
    `evidence_urls` TEXT DEFAULT NULL COMMENT '证据图片URL列表（JSON格式）',
    `report_status` ENUM('pending', 'processing', 'resolved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '处理状态',
    `handled_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '处理管理员ID',
    `handle_result` VARCHAR(255) DEFAULT NULL COMMENT '处理结果说明',
    `handled_at` DATETIME DEFAULT NULL COMMENT '处理时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_reporter_id` (`reporter_id`),
    INDEX `idx_report_status` (`report_status`),
    INDEX `idx_handled_by` (`handled_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛举报表';

-- 13. 论坛审核日志表
DROP TABLE IF EXISTS `forum_audit_log`;
CREATE TABLE `forum_audit_log` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `target_type` ENUM('post', 'comment') NOT NULL COMMENT '审核目标类型',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
    `auditor_id` BIGINT UNSIGNED NOT NULL COMMENT '审核员ID',
    `action` ENUM('approve', 'reject', 'hide', 'delete', 'restore') NOT NULL COMMENT '审核操作',
    `reason` VARCHAR(255) DEFAULT NULL COMMENT '审核原因/驳回原因',
    `old_status` VARCHAR(20) DEFAULT NULL COMMENT '旧状态',
    `new_status` VARCHAR(20) DEFAULT NULL COMMENT '新状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_auditor_id` (`auditor_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛审核日志表';

-- 14. 帖子日浏览统计表
DROP TABLE IF EXISTS `forum_post_view_daily`;
CREATE TABLE `forum_post_view_daily` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '浏览统计ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `uv_count` INT NOT NULL DEFAULT 0 COMMENT '独立访客数',
    `pv_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_date` (`post_id`, `stat_date`),
    INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子日浏览统计表';

-- 15. 用户关注标签表
DROP TABLE IF EXISTS `forum_follow_tag`;
CREATE TABLE `forum_follow_tag` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关注ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注标签表';

-- ======================================================
-- 第三部分：初始化数据
-- ======================================================

-- 插入默认管理员账号（密码：admin123，实际使用时需要加密）
-- 注意：实际部署时请使用加密后的密码
INSERT INTO `user_account` (`username`, `nickname`, `phone`, `email`, `password_hash`, `is_admin`, `user_status`)
VALUES 
('admin', 'admin', '13800000000', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, 'active');

-- 插入默认论坛分类
INSERT INTO `forum_category` (`parent_id`, `name`, `icon`, `sort_order`, `is_enabled`) VALUES
(0, '二手交易', 'category_trade', 1, 1),
(0, '经验分享', 'category_share', 2, 1),
(0, '求助问答', 'category_help', 3, 1),
(0, '闲聊灌水', 'category_chat', 4, 1),
(1, '手机数码', NULL, 1, 1),
(1, '家居日用', NULL, 2, 1),
(1, '服饰鞋包', NULL, 3, 1),
(1, '母婴育儿', NULL, 4, 1);

-- 插入默认标签
INSERT INTO `forum_tag` (`tag_name`, `tag_icon`, `sort_order`, `is_enabled`) VALUES
('热门', 'hot', 1, 1),
('精华', 'essence', 2, 1),
('求助', 'help', 3, 1),
('已解决', 'solved', 4, 1),
('干货', 'dry_goods', 5, 1),
('避坑', 'avoid_pit', 6, 1),
('砍价', 'bargain', 7, 1),
('验机', 'check', 8, 1);

-- ======================================================
-- 第四部分：存储过程与触发器
-- ======================================================

-- 触发器：新增评论时更新帖子的评论数和最后评论时间
DROP TRIGGER IF EXISTS `trg_forum_comment_insert`;
DELIMITER $$
CREATE TRIGGER `trg_forum_comment_insert`
AFTER INSERT ON `forum_comment`
FOR EACH ROW
BEGIN
    IF NEW.audit_status = 'approved' AND NEW.is_deleted = 0 THEN
        UPDATE `forum_post` 
        SET `comment_count` = `comment_count` + 1,
            `last_commented_at` = NEW.created_at
        WHERE `id` = NEW.post_id;
    END IF;
END$$
DELIMITER ;

-- 触发器：删除评论时更新帖子的评论数
DROP TRIGGER IF EXISTS `trg_forum_comment_delete`;
DELIMITER $$
CREATE TRIGGER `trg_forum_comment_delete`
AFTER UPDATE ON `forum_comment`
FOR EACH ROW
BEGIN
    IF OLD.is_deleted = 0 AND NEW.is_deleted = 1 AND OLD.audit_status = 'approved' THEN
        UPDATE `forum_post` 
        SET `comment_count` = `comment_count` - 1
        WHERE `id` = NEW.post_id;
    END IF;
END$$
DELIMITER ;

-- 触发器：新增点赞时更新帖子的点赞数
DROP TRIGGER IF EXISTS `trg_forum_reaction_insert`;
DELIMITER $$
CREATE TRIGGER `trg_forum_reaction_insert`
AFTER INSERT ON `forum_reaction`
FOR EACH ROW
BEGIN
    IF NEW.reaction_type = 'like' THEN
        IF NEW.target_type = 'post' THEN
            UPDATE `forum_post` SET `like_count` = `like_count` + 1 WHERE `id` = NEW.target_id;
        ELSEIF NEW.target_type = 'comment' THEN
            UPDATE `forum_comment` SET `like_count` = `like_count` + 1 WHERE `id` = NEW.target_id;
        END IF;
    END IF;
END$$
DELIMITER ;

-- 触发器：取消点赞时更新帖子的点赞数
DROP TRIGGER IF EXISTS `trg_forum_reaction_delete`;
DELIMITER $$
CREATE TRIGGER `trg_forum_reaction_delete`
AFTER DELETE ON `forum_reaction`
FOR EACH ROW
BEGIN
    IF OLD.reaction_type = 'like' THEN
        IF OLD.target_type = 'post' THEN
            UPDATE `forum_post` SET `like_count` = `like_count` - 1 WHERE `id` = OLD.target_id;
        ELSEIF OLD.target_type = 'comment' THEN
            UPDATE `forum_comment` SET `like_count` = `like_count` - 1 WHERE `id` = OLD.target_id;
        END IF;
    END IF;
END$$
DELIMITER ;

-- ======================================================
-- 第五部分：常用查询视图
-- ======================================================

-- 帖子列表视图（包含作者信息）
DROP VIEW IF EXISTS `v_forum_post_list`;
CREATE VIEW `v_forum_post_list` AS
SELECT 
    p.id,
    p.title,
    p.content,
    p.post_type,
    p.like_count,
    p.comment_count,
    p.share_count,
    p.collect_count,
    p.view_count,
    p.published_at,
    p.created_at,
    p.audit_status,
    p.display_status,
    ua.id AS author_id,
    ua.username AS author_name,
    up.avatar_url AS author_avatar,
    COALESCE(up.credit_score, 100) AS author_credit_score,
    GROUP_CONCAT(DISTINCT t.tag_name SEPARATOR ',') AS tags,
    c.name AS category_name
FROM forum_post p
LEFT JOIN `user_account` ua ON p.author_id = ua.id
LEFT JOIN `user_profile` up ON p.author_id = up.user_id
LEFT JOIN forum_post_tag pt ON p.id = pt.post_id
LEFT JOIN forum_tag t ON pt.tag_id = t.id
LEFT JOIN forum_category c ON p.category_id = c.id
WHERE p.is_deleted = 0
GROUP BY p.id;

-- 帖子详情视图（完整信息）
DROP VIEW IF EXISTS `v_forum_post_detail`;
CREATE VIEW `v_forum_post_detail` AS
SELECT 
    p.*,
    ua.username AS author_name,
    up.avatar_url AS author_avatar,
    COALESCE(up.credit_score, 100) AS author_credit_score,
    up.bio AS author_bio,
    ua.created_at AS author_join_time,
    (SELECT COUNT(*) FROM forum_post WHERE author_id = p.author_id AND is_deleted = 0) AS author_post_count,
    (SELECT COUNT(*) FROM forum_follow_tag WHERE tag_id IN (SELECT tag_id FROM forum_post_tag WHERE post_id = p.id)) AS tag_follow_count
FROM forum_post p
LEFT JOIN `user_account` ua ON p.author_id = ua.id
LEFT JOIN `user_profile` up ON p.author_id = up.user_id;

-- ======================================================
-- 第六部分：测试数据
-- ======================================================

-- 1. 插入测试用户（密码统一为：123456，实际已加密）
-- 注意：实际使用时密码需要正确加密，这里使用占位符
INSERT INTO `user_account` (`id`, `username`, `nickname`, `phone`, `email`, `password_hash`, `is_admin`, `user_status`) VALUES
(10001, '张三', '张三', '13800000001', 'zhangsan@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10002, '李四', '李四', '13800000002', 'lisi@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10003, '王五', '王五', '13800000003', 'wangwu@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10004, '赵六', '赵六', '13800000004', 'zhaoliu@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10005, '小明', '小明', '13800000005', 'xiaoming@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10006, '小红', '小红', '13800000006', 'xiaohong@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'banned'),
(10007, '大刘', '大刘', '13800000007', 'daliu@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active'),
(10008, '小陈', '小陈', '13800000008', 'xiaochen@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 0, 'active');

INSERT INTO `user_profile` (`user_id`, `avatar_url`, `bio`, `credit_score`) VALUES
(10001, '/avatars/zhangsan.jpg', 'Digital goods enthusiast, trusted trader', 95),
(10002, '/avatars/lisi.jpg', 'Student buyer looking for used textbooks', 88),
(10003, '/avatars/wangwu.jpg', 'Professional seller with fair prices', 92),
(10004, '/avatars/zhaoliu.jpg', 'New to the platform, nice to meet you', 75),
(10005, '/avatars/xiaoming.jpg', 'Enjoys hunting for second-hand deals', 85),
(10006, '/avatars/xiaohong.jpg', 'Focuses on sharing mother-and-baby products', 60),
(10007, '/avatars/daliu.jpg', 'Photography lover and gear collector', 98),
(10008, '/avatars/xiaochen.jpg', 'Bargain hunter', 82);

-- 2. 插入测试帖子
INSERT INTO `forum_post` (`id`, `author_id`, `category_id`, `post_type`, `title`, `content`, `audit_status`, `display_status`, `like_count`, `comment_count`, `share_count`, `collect_count`, `view_count`, `published_at`) VALUES
(10001, 10001, 2, 'review', 'iPhone 13 Pro 二手购买避坑指南', '最近在平台淘了一台iPhone 13 Pro，分享一下我的验机经验：\n\n1. 首先检查外观，看是否有拆修痕迹\n2. 用爱思助手查看电池健康度和是否全原装\n3. 测试面容ID、相机扬声器等功能\n4. 查询序列号确认保修状态\n\n希望对大家有帮助。', 'approved', 'normal', 45, 12, 8, 23, 1560, '2026-03-20 10:30:00'),
(10002, 10002, 3, 'help', '想买二手笔记本电脑，预算3000以内有什么推荐？', '本人学生党，预算3000左右想买一台二手笔记本，主要用于写论文、看视频、偶尔用PS。请问各位大佬有什么推荐？需要注意什么？', 'approved', 'normal', 23, 18, 5, 9, 890, '2026-03-22 14:15:00'),
(10003, 10003, 1, 'sell', '9成新Switch OLED版出售', '今年3月购入，几乎没么玩，箱说全，带两个游戏（塞尔达+马里奥奥德赛）\n\n成色：屏幕贴膜，机身无划痕\n价格：2800元可小刀\n交易方式：支持面交或邮寄\n\n有意者私聊', 'approved', 'normal', 67, 25, 15, 42, 2340, '2026-03-25 09:00:00'),
(10004, 10004, 4, 'normal', '在平台第一次卖东西就被骗了，大家小心', '事情是这样的：有人私聊我说要买我的旧手机，然后让我加微信，发了个假链接让我点...\n\n提醒各位新手卖家，任何让你点击外部链接的行为都要警惕。', 'approved', 'normal', 89, 32, 28, 56, 3420, '2026-03-18 20:45:00'),
(10005, 10005, 2, 'review', '二手Kindle到底值不值得买？', '最近入手了一台二手Kindle Paperwhite 4，价格900块\n\n优点：\n- 墨水屏护眼\n- 续航给力\n- 价格便宜\n\n缺点：\n- 翻页慢\n- 不支持彩色\n\n建议：喜欢阅读的可以入手，追求体验的建议买新款。', 'approved', 'normal', 34, 9, 6, 18, 1200, '2026-03-28 16:20:00'),
(10006, 10006, 1, 'sell', '闲置婴儿推车，九成新', '宝宝长大了用不上了，好孩子品牌，可坐可躺，带遮阳棚\n\n使用时间：约6个月\n成色：九成新，有正常使用痕迹\n价格：80元\n\n限自提，坐标深圳南山', 'pending', 'normal', 0, 0, 0, 0, 45, NULL),
(10007, 10007, 2, 'review', '二手相机购买经验分享，教你如何避坑', '玩摄影三年，前前后后买了5台二手相机，分享一下我的经验：\n\n1. 快门数不是唯一标准\n2. 一定要检查CMOS有没有坏点\n3. 镜头要对焦测试\n4. 最好面交，当场测试\n5. 保留聊天记录和交易凭证\n\n欢迎大家补充。', 'approved', 'featured', 156, 45, 32, 89, 5670, '2026-03-15 11:00:00'),
(10008, 10008, 3, 'help', '卖二手手机怎么定价？求指导', '手里有一台iPhone 12 256G，用了两年，电池健康82%，外观无磕碰。\n\n想问一下大概能卖多少钱？在哪里看参考价格比较准？', 'approved', 'normal', 12, 8, 2, 5, 567, '2026-03-30 08:30:00'),
(10009, 10001, 4, 'normal', '聊聊平台最近的变化，越来越好用了', '用了这个平台一年多了，最近发现几个改进：\n\n- 聊天功能更稳定了\n- 审核速度快了很多\n- 增加了AI助手功能\n\n希望平台越来越好。', 'approved', 'normal', 28, 6, 3, 11, 890, '2026-03-29 19:00:00'),
(10010, 10003, 1, 'sell', '搬家甩卖：宜家书桌+椅子', '因搬家需要清空，宜家购买的书桌和椅子，使用一年\n\n书桌尺寸：120*60cm\n成色：八成新\n价格：书桌150元，椅子80元，一起200元\n\n需要自提，坐标上海徐汇', 'approved', 'top', 34, 15, 7, 28, 1560, '2026-03-27 13:30:00');

-- 3. 插入帖子标签关联
INSERT INTO `forum_post_tag` (`post_id`, `tag_id`) VALUES
(10001, 1), (10001, 5), (10001, 8),
(10002, 3), (10002, 7),
(10003, 1), (10003, 7),
(10004, 6), (10004, 1),
(10005, 5), (10005, 8),
(10006, 1), (10006, 4),
(10007, 1), (10007, 2), (10007, 5),
(10008, 3), (10008, 7),
(10009, 1), (10009, 4),
(10010, 1), (10010, 4);

-- 4. 插入帖子媒体附件
INSERT INTO `forum_post_media` (`post_id`, `media_type`, `media_url`, `sort_no`) VALUES
(10001, 'image', '/uploads/posts/10001_1.jpg', 1),
(10001, 'image', '/uploads/posts/10001_2.jpg', 2),
(10003, 'image', '/uploads/posts/10003_1.jpg', 1),
(10003, 'image', '/uploads/posts/10003_2.jpg', 2),
(10003, 'image', '/uploads/posts/10003_3.jpg', 3),
(10005, 'image', '/uploads/posts/10005_1.jpg', 1),
(10007, 'image', '/uploads/posts/10007_1.jpg', 1),
(10007, 'image', '/uploads/posts/10007_2.jpg', 2),
(10010, 'image', '/uploads/posts/10010_1.jpg', 1);

-- 5. 插入评论数据
INSERT INTO `forum_comment` (`id`, `post_id`, `parent_comment_id`, `reply_to_user_id`, `commenter_id`, `content`, `audit_status`, `like_count`, `reply_count`) VALUES
(10001, 10001, 0, NULL, 10002, '感谢分享，很实用！正准备买二手iPhone', 'approved', 8, 2),
(10002, 10001, 10001, 10002, 10003, '爱助手可以改数据，建议用沙漏验机更准', 'approved', 12, 1),
(10003, 10001, 10002, 10003, 10001, '谢谢提醒，沙漏确实更靠谱', 'approved', 3, 0),
(10004, 10001, 0, NULL, 10007, '写得很好，已收藏', 'approved', 5, 0),
(10005, 10002, 0, NULL, 10001, '推荐ThinkPad X1 Carbon 2018款，3000左右能拿下', 'approved', 15, 3),
(10006, 10002, 10005, 10001, 10002, '这个型号好用吗？散热怎么样？', 'approved', 2, 1),
(10007, 10002, 10006, 10002, 10001, '散热还不错，办公完全够用', 'approved', 1, 0),
(10008, 10002, 0, NULL, 10007, '建议加点预算上M1 MacBook Air', 'approved', 8, 0),
(10009, 10003, 0, NULL, 10001, '价格还能优惠吗？坐标哪里？', 'approved', 3, 1),
(10010, 10003, 10009, 10001, 10003, '可以小刀，坐标北京朝阳', 'approved', 2, 0),
(10011, 10004, 0, NULL, 10001, '感谢提醒，骗子太可恶了', 'approved', 15, 0),
(10012, 10004, 0, NULL, 10007, '建议平台完善防骗机制', 'approved', 8, 0),
(10013, 10007, 0, NULL, 10001, '大佬写得太好了！已收藏+转发', 'approved', 12, 1),
(10014, 10007, 10013, 10001, 10002, '谢谢支持，有问题可以问我', 'approved', 3, 0),
(10015, 10010, 0, NULL, 10002, '书桌还在吗？我周末可以自提', 'approved', 2, 1);

-- 6. 更新帖子的评论数
UPDATE forum_post SET comment_count = (SELECT COUNT(*) FROM forum_comment WHERE post_id = forum_post.id AND audit_status = 'approved') WHERE id IN (10001,10002,10003,10004,10007,10010);

-- 7. 插入互动数据（点赞/收藏/转发）
INSERT INTO `forum_reaction` (`target_type`, `target_id`, `user_id`, `reaction_type`) VALUES
('post', 10001, 10002, 'like'), ('post', 10001, 10003, 'like'), ('post', 10001, 10005, 'like'),
('post', 10003, 10001, 'like'), ('post', 10003, 10002, 'like'), ('post', 10003, 10004, 'like'), ('post', 10003, 10007, 'like'),
('post', 10007, 10001, 'like'), ('post', 10007, 10002, 'like'), ('post', 10007, 10003, 'like'),
('comment', 10001, 10003, 'like'), ('comment', 10001, 10005, 'like'),
('comment', 10005, 10002, 'like'), ('comment', 10005, 10007, 'like');

INSERT INTO `forum_collect` (`user_id`, `post_id`) VALUES
(10001, 10007), (10002, 10007), (10003, 10007),
(10002, 10001), (10005, 10001),
(10001, 10003), (10007, 10003);

INSERT INTO `forum_post_share` (`post_id`, `user_id`, `share_channel`) VALUES
(10007, 10001, 'wechat'), (10007, 10002, 'qq'), (10007, 10003, 'weibo'),
(10001, 10005, 'in_app'), (10003, 10001, 'wechat');

-- 8. 插入举报数据
INSERT INTO `forum_report` (`target_type`, `target_id`, `reporter_id`, `report_reason`, `report_detail`, `report_status`, `handled_by`, `handle_result`, `handled_at`) VALUES
('post', 10004, 10002, '广告', '帖子内容含有外部链接，疑似引流', 'resolved', 1, '已处理，帖子正常，无违规', '2026-03-19 10:00:00'),
('post', 10006, 10003, '欺诈', '描述与实物不符的嫌疑', 'pending', NULL, NULL, NULL);

-- 9. 插入审核日志
INSERT INTO `forum_audit_log` (`target_type`, `target_id`, `auditor_id`, `action`, `reason`, `old_status`, `new_status`) VALUES
('post', 10006, 1, 'reject', '商品图片不清晰，请重新上传', 'pending', 'rejected');

-- 10. 插入日浏览统计数据
INSERT INTO `forum_post_view_daily` (`post_id`, `stat_date`, `uv_count`, `pv_count`) VALUES
(10001, '2026-03-28', 120, 345),
(10001, '2026-03-29', 89, 234),
(10003, '2026-03-28', 156, 456),
(10007, '2026-03-28', 234, 678);

-- 11. 插入用户关注标签
INSERT INTO `forum_follow_tag` (`user_id`, `tag_id`) VALUES
(10001, 1), (10001, 2), (10001, 5),
(10002, 1), (10002, 3),
(10003, 1), (10003, 7),
(10007, 1), (10007, 2), (10007, 5), (10007, 8);

-- =========================
-- 商品域
-- =========================
CREATE TABLE category (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号（越小越靠前）',
  is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0否1是',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表（单层基础分类）';

CREATE TABLE product (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家用户ID（来自user_account，可与买家身份共存）',
  category_id BIGINT UNSIGNED NOT NULL COMMENT '商品分类ID',
  title VARCHAR(150) NOT NULL COMMENT '商品标题',
  subtitle VARCHAR(255) NULL COMMENT '商品副标题',
  description TEXT NOT NULL COMMENT '商品详细描述',
  brand VARCHAR(100) NULL COMMENT '品牌',
  model VARCHAR(100) NULL COMMENT '型号',
  condition_level ENUM('new','almost_new','good','fair','poor') NOT NULL COMMENT '新旧程度',
  purchase_year SMALLINT NULL COMMENT '购买年份',
  original_price DECIMAL(10,2) NULL COMMENT '原价（元）',
  selling_price DECIMAL(10,2) NOT NULL COMMENT '出售价格（元）',
  can_bargain TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否可议价：0否1是',
  trade_mode ENUM('pickup','shipping','both') NOT NULL DEFAULT 'both' COMMENT '交易方式：自提/邮寄/都支持',
  pickup_city VARCHAR(100) NULL COMMENT '自提城市',
  pickup_address VARCHAR(255) NULL COMMENT '自提地点描述',
  location_lat DECIMAL(10,7) NULL COMMENT '卖家纬度（用于距离筛选）',
  location_lng DECIMAL(10,7) NULL COMMENT '卖家经度（用于距离筛选）',
  stock INT NOT NULL DEFAULT 1 COMMENT '库存数量（二手通常为1）',
  publish_status ENUM('draft','pending_review','on_sale','reserved','sold','off_shelf','rejected','deleted') NOT NULL DEFAULT 'pending_review' COMMENT '发布状态',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
  published_at DATETIME NULL COMMENT '上架时间',
  off_shelf_at DATETIME NULL COMMENT '下架时间',
  reject_reason VARCHAR(255) NULL COMMENT '审核驳回原因',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_product_seller (seller_id),
  KEY idx_product_category (category_id),
  KEY idx_product_price (selling_price),
  KEY idx_product_status (publish_status),
  KEY idx_product_publish_time (published_at),
  FULLTEXT KEY ft_product_search (title, subtitle, description),
  CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES user_account(id),
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
  CONSTRAINT chk_product_price CHECK (selling_price >= 0),
  CONSTRAINT chk_product_stock CHECK (stock >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品主表';

CREATE TABLE product_image (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '商品图片ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
  is_cover TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否封面图：0否1是',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_product_image_product (product_id),
  KEY idx_product_image_cover (product_id, is_cover),
  CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表（多图）';

CREATE TABLE product_favorite (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '买家ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_product_favorite_user_product (user_id, product_id),
  KEY idx_product_favorite_product (product_id),
  CONSTRAINT fk_product_favorite_user FOREIGN KEY (user_id) REFERENCES user_account(id),
  CONSTRAINT fk_product_favorite_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

CREATE TABLE seller_follow (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
  buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家ID（关注者）',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家ID（被关注者）',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  UNIQUE KEY uk_seller_follow_buyer_seller (buyer_id, seller_id),
  KEY idx_seller_follow_seller (seller_id),
  CONSTRAINT fk_seller_follow_buyer FOREIGN KEY (buyer_id) REFERENCES user_account(id),
  CONSTRAINT fk_seller_follow_seller FOREIGN KEY (seller_id) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卖家关注表';

-- =========================
-- IM聊天域
-- =========================
CREATE TABLE conversation (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
  conversation_type ENUM('product_consult','order_service','system') NOT NULL DEFAULT 'product_consult' COMMENT '会话类型',
  product_id BIGINT UNSIGNED NULL COMMENT '关联商品ID（商品咨询场景）',
  order_id BIGINT UNSIGNED NULL COMMENT '关联订单ID（售后场景）',
  last_message_at DATETIME NULL COMMENT '最后消息时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_conversation_product (product_id),
  KEY idx_conversation_order (order_id),
  CONSTRAINT fk_conversation_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

CREATE TABLE conversation_member (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '会话成员ID',
  conversation_id BIGINT UNSIGNED NOT NULL COMMENT '会话ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  role_in_conversation ENUM('buyer','seller','admin','system') NOT NULL COMMENT '会话内角色',
  joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  muted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否免打扰',
  new_conversation_flag TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否新会话提示：0否1是',
  unread_message_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '未读消息数',
  last_read_at DATETIME NULL COMMENT '最后已读时间',
  UNIQUE KEY uk_conversation_member (conversation_id, user_id),
  KEY idx_conversation_member_unread (user_id, new_conversation_flag, unread_message_count),
  KEY idx_conversation_member_user (user_id),
  CONSTRAINT fk_conversation_member_conversation FOREIGN KEY (conversation_id) REFERENCES conversation(id),
  CONSTRAINT fk_conversation_member_user FOREIGN KEY (user_id) REFERENCES user_account(id),
  CONSTRAINT chk_conversation_member_new_flag CHECK (new_conversation_flag IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话成员表';

CREATE TABLE chat_message (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  conversation_id BIGINT UNSIGNED NOT NULL COMMENT '会话ID',
  sender_id BIGINT UNSIGNED NOT NULL COMMENT '发送者用户ID',
  message_type ENUM('text','image','system','order_card','product_card') NOT NULL DEFAULT 'text' COMMENT '消息类型',
  content TEXT NOT NULL COMMENT '消息内容',
  ext_json JSON NULL COMMENT '扩展信息（如商品卡片、订单卡片）',
  sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  recalled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否撤回',
  KEY idx_chat_message_conversation_time (conversation_id, sent_at),
  KEY idx_chat_message_sender (sender_id),
  CONSTRAINT fk_chat_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversation(id),
  CONSTRAINT fk_chat_message_sender FOREIGN KEY (sender_id) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- =========================
-- 交易与订单域
-- =========================
CREATE TABLE trade_order (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(64) NOT NULL COMMENT '订单号（业务唯一）',
  buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家用户ID（来自user_account）',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家用户ID（来自user_account）',
  order_status ENUM('pending_payment','paid_pending_ship','shipped','delivered','completed','cancelled','refund_in_progress','closed') NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态',
  trade_mode ENUM('pickup','shipping') NOT NULL COMMENT '本订单交易方式',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额（元）',
  freight_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费金额（元）',
  pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额（元）',
  remark VARCHAR(255) NULL COMMENT '买家备注',
  receiver_name VARCHAR(50) NULL COMMENT '收货人姓名（邮寄）',
  receiver_phone VARCHAR(20) NULL COMMENT '收货人电话（邮寄）',
  receiver_address VARCHAR(255) NULL COMMENT '收货地址（邮寄）',
  pickup_location VARCHAR(255) NULL COMMENT '自提地点（自提）',
  cancel_reason VARCHAR(255) NULL COMMENT '取消原因',
  paid_at DATETIME NULL COMMENT '支付完成时间',
  shipped_at DATETIME NULL COMMENT '发货时间',
  delivered_at DATETIME NULL COMMENT '签收时间',
  completed_at DATETIME NULL COMMENT '完成时间（确认收货）',
  cancelled_at DATETIME NULL COMMENT '取消时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_trade_order_no (order_no),
  KEY idx_trade_order_buyer (buyer_id, created_at),
  KEY idx_trade_order_seller (seller_id, created_at),
  KEY idx_trade_order_status (order_status),
  CONSTRAINT fk_trade_order_buyer FOREIGN KEY (buyer_id) REFERENCES user_account(id),
  CONSTRAINT fk_trade_order_seller FOREIGN KEY (seller_id) REFERENCES user_account(id),
  CONSTRAINT chk_trade_order_amount CHECK (total_amount >= 0 AND freight_amount >= 0 AND pay_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

CREATE TABLE order_item (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  product_title VARCHAR(150) NOT NULL COMMENT '下单时商品标题快照',
  product_image_url VARCHAR(500) NULL COMMENT '下单时封面图快照',
  unit_price DECIMAL(10,2) NOT NULL COMMENT '成交单价（元）',
  quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  subtotal_amount DECIMAL(10,2) NOT NULL COMMENT '小计金额（元）',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_order_item_order (order_id),
  KEY idx_order_item_product (product_id),
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id),
  CONSTRAINT chk_order_item_quantity CHECK (quantity > 0),
  CONSTRAINT chk_order_item_amount CHECK (unit_price >= 0 AND subtotal_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表（支持多商品扩展）';

CREATE TABLE payment_order (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '支付单ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  payment_no VARCHAR(64) NOT NULL COMMENT '支付单号（业务唯一）',
  payment_channel ENUM('wechat','alipay','balance') NOT NULL COMMENT '支付渠道',
  payment_status ENUM('created','paying','paid','failed','closed','refunded') NOT NULL DEFAULT 'created' COMMENT '支付状态',
  payable_amount DECIMAL(10,2) NOT NULL COMMENT '应付金额（元）',
  paid_amount DECIMAL(10,2) NULL COMMENT '实付金额（元）',
  channel_trade_no VARCHAR(100) NULL COMMENT '三方支付流水号',
  paid_at DATETIME NULL COMMENT '支付完成时间',
  failed_reason VARCHAR(255) NULL COMMENT '支付失败原因',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_payment_order_no (payment_no),
  UNIQUE KEY uk_payment_order_order (order_id),
  KEY idx_payment_order_status (payment_status),
  CONSTRAINT fk_payment_order_trade_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT chk_payment_order_amount CHECK (payable_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单表';

CREATE TABLE payment_transaction (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '支付交易流水ID',
  payment_order_id BIGINT UNSIGNED NOT NULL COMMENT '支付单ID',
  transaction_type ENUM('pay','refund','adjust') NOT NULL COMMENT '交易类型',
  transaction_status ENUM('processing','success','failed') NOT NULL COMMENT '交易状态',
  amount DECIMAL(10,2) NOT NULL COMMENT '交易金额（元）',
  channel_trade_no VARCHAR(100) NULL COMMENT '渠道流水号',
  channel_response JSON NULL COMMENT '渠道响应报文',
  occurred_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  KEY idx_payment_transaction_order (payment_order_id, occurred_at),
  CONSTRAINT fk_payment_transaction_payment_order FOREIGN KEY (payment_order_id) REFERENCES payment_order(id),
  CONSTRAINT chk_payment_transaction_amount CHECK (amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付交易流水表';

CREATE TABLE escrow_record (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '担保资金记录ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  escrow_status ENUM('frozen','released_to_seller','refund_to_buyer') NOT NULL DEFAULT 'frozen' COMMENT '担保状态',
  frozen_amount DECIMAL(10,2) NOT NULL COMMENT '冻结金额（元）',
  released_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已释放金额（元）',
  refunded_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已退款金额（元）',
  frozen_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '冻结时间',
  released_at DATETIME NULL COMMENT '解冻到账时间',
  refunded_at DATETIME NULL COMMENT '退款完成时间',
  UNIQUE KEY uk_escrow_record_order (order_id),
  CONSTRAINT fk_escrow_record_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT chk_escrow_record_amount CHECK (frozen_amount >= 0 AND released_amount >= 0 AND refunded_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='担保资金表（买家确认前平台暂存）';

CREATE TABLE order_shipment (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '发货记录ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  shipment_type ENUM('shipping','pickup') NOT NULL COMMENT '交付类型',
  logistics_company VARCHAR(100) NULL COMMENT '物流公司',
  tracking_no VARCHAR(100) NULL COMMENT '物流单号',
  shipment_status ENUM('to_ship','in_transit','signed','lost','exception') NOT NULL DEFAULT 'to_ship' COMMENT '物流状态',
  shipped_by BIGINT UNSIGNED NULL COMMENT '操作发货用户ID',
  shipped_at DATETIME NULL COMMENT '发货时间',
  signed_at DATETIME NULL COMMENT '签收时间',
  pickup_code VARCHAR(20) NULL COMMENT '自提码（自提场景）',
  pickup_verified_at DATETIME NULL COMMENT '自提核销时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_order_shipment_order (order_id),
  KEY idx_order_shipment_tracking (tracking_no),
  CONSTRAINT fk_order_shipment_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_order_shipment_user FOREIGN KEY (shipped_by) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单发货与交付表';

CREATE TABLE logistics_trace (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '物流轨迹ID',
  shipment_id BIGINT UNSIGNED NOT NULL COMMENT '发货记录ID',
  trace_time DATETIME NOT NULL COMMENT '轨迹时间',
  trace_status VARCHAR(100) NOT NULL COMMENT '轨迹状态',
  trace_detail VARCHAR(500) NOT NULL COMMENT '轨迹详情',
  trace_location VARCHAR(255) NULL COMMENT '轨迹地点',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_logistics_trace_shipment_time (shipment_id, trace_time),
  CONSTRAINT fk_logistics_trace_shipment FOREIGN KEY (shipment_id) REFERENCES order_shipment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹明细表';

CREATE TABLE order_status_log (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '订单状态日志ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  from_status VARCHAR(50) NULL COMMENT '变更前状态',
  to_status VARCHAR(50) NOT NULL COMMENT '变更后状态',
  changed_by BIGINT UNSIGNED NULL COMMENT '操作人用户ID',
  change_reason VARCHAR(255) NULL COMMENT '变更原因',
  changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  KEY idx_order_status_log_order (order_id, changed_at),
  CONSTRAINT fk_order_status_log_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_order_status_log_user FOREIGN KEY (changed_by) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态流转日志表';

ALTER TABLE conversation
  ADD CONSTRAINT fk_conversation_order FOREIGN KEY (order_id) REFERENCES trade_order(id);

-- =========================
-- 资金账户域（卖家收款/提现）
-- =========================
CREATE TABLE wallet_account (
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

CREATE TABLE wallet_ledger (
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

CREATE TABLE withdrawal_request (
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

-- =========================
-- 评价与售后域
-- =========================
CREATE TABLE review (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  order_item_id BIGINT UNSIGNED NOT NULL COMMENT '订单明细ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家ID（评价人）',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家ID（被评人）',
  rating TINYINT UNSIGNED NOT NULL COMMENT '评分（1-5）',
  content VARCHAR(1000) NULL COMMENT '评价内容',
  is_anonymous TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否匿名评价',
  has_sensitive_content TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否命中敏感内容',
  seller_reply VARCHAR(1000) NULL COMMENT '卖家回复',
  seller_reply_at DATETIME NULL COMMENT '卖家回复时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_review_order_item (order_item_id),
  KEY idx_review_seller (seller_id, created_at),
  KEY idx_review_buyer (buyer_id, created_at),
  KEY idx_review_product (product_id),
  CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES order_item(id),
  CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES product(id),
  CONSTRAINT fk_review_buyer FOREIGN KEY (buyer_id) REFERENCES user_account(id),
  CONSTRAINT fk_review_seller FOREIGN KEY (seller_id) REFERENCES user_account(id),
  CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

CREATE TABLE review_image (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '评价图片ID',
  review_id BIGINT UNSIGNED NOT NULL COMMENT '评价ID',
  image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_review_image_review (review_id),
  CONSTRAINT fk_review_image_review FOREIGN KEY (review_id) REFERENCES review(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价图片表';

CREATE TABLE after_sale_request (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '售后申请ID',
  after_sale_no VARCHAR(64) NOT NULL COMMENT '售后单号（业务唯一）',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  order_item_id BIGINT UNSIGNED NOT NULL COMMENT '订单明细ID',
  buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家ID（申请人）',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家ID',
  request_type ENUM('return_refund','refund_only','exchange','complaint') NOT NULL COMMENT '售后类型',
  request_reason VARCHAR(255) NOT NULL COMMENT '申请原因',
  detail_desc VARCHAR(1000) NULL COMMENT '问题描述',
  requested_amount DECIMAL(10,2) NULL COMMENT '申请退款金额（元）',
  final_amount DECIMAL(10,2) NULL COMMENT '最终退款金额（元）',
  request_status ENUM('pending_seller','pending_admin','approved','rejected','cancelled','completed') NOT NULL DEFAULT 'pending_seller' COMMENT '售后状态',
  seller_response VARCHAR(1000) NULL COMMENT '卖家处理意见',
  seller_responded_at DATETIME NULL COMMENT '卖家响应时间',
  admin_id BIGINT UNSIGNED NULL COMMENT '处理管理员ID',
  admin_decision VARCHAR(1000) NULL COMMENT '管理员裁决意见',
  closed_at DATETIME NULL COMMENT '关闭时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_after_sale_no (after_sale_no),
  KEY idx_after_sale_order (order_id),
  KEY idx_after_sale_buyer (buyer_id, created_at),
  KEY idx_after_sale_seller (seller_id, created_at),
  KEY idx_after_sale_status (request_status),
  CONSTRAINT fk_after_sale_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_after_sale_order_item FOREIGN KEY (order_item_id) REFERENCES order_item(id),
  CONSTRAINT fk_after_sale_buyer FOREIGN KEY (buyer_id) REFERENCES user_account(id),
  CONSTRAINT fk_after_sale_seller FOREIGN KEY (seller_id) REFERENCES user_account(id),
  CONSTRAINT fk_after_sale_admin FOREIGN KEY (admin_id) REFERENCES user_account(id),
  CONSTRAINT chk_after_sale_amount CHECK (requested_amount IS NULL OR requested_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后申请表';

CREATE TABLE after_sale_evidence (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '售后凭证ID',
  after_sale_id BIGINT UNSIGNED NOT NULL COMMENT '售后申请ID',
  evidence_type ENUM('image','video','text','logistics_doc') NOT NULL COMMENT '凭证类型',
  content_url VARCHAR(500) NULL COMMENT '凭证文件URL',
  content_text VARCHAR(1000) NULL COMMENT '文本说明',
  uploaded_by BIGINT UNSIGNED NOT NULL COMMENT '上传用户ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  KEY idx_after_sale_evidence_after_sale (after_sale_id),
  CONSTRAINT fk_after_sale_evidence_after_sale FOREIGN KEY (after_sale_id) REFERENCES after_sale_request(id),
  CONSTRAINT fk_after_sale_evidence_user FOREIGN KEY (uploaded_by) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后凭证表';

CREATE TABLE dispute_case (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '纠纷单ID',
  dispute_no VARCHAR(64) NOT NULL COMMENT '纠纷单号（业务唯一）',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  after_sale_id BIGINT UNSIGNED NULL COMMENT '关联售后ID',
  buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家ID',
  seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家ID',
  current_status ENUM('open','investigating','waiting_evidence','resolved','closed') NOT NULL DEFAULT 'open' COMMENT '纠纷状态',
  responsibility ENUM('buyer','seller','both','platform','undetermined') NOT NULL DEFAULT 'undetermined' COMMENT '责任判定',
  resolution_result VARCHAR(1000) NULL COMMENT '处理结果',
  resolution_amount DECIMAL(10,2) NULL COMMENT '裁定退款金额（元）',
  resolved_by BIGINT UNSIGNED NULL COMMENT '处理管理员ID',
  resolved_at DATETIME NULL COMMENT '处理完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_dispute_case_no (dispute_no),
  KEY idx_dispute_case_order (order_id),
  KEY idx_dispute_case_status (current_status),
  CONSTRAINT fk_dispute_case_order FOREIGN KEY (order_id) REFERENCES trade_order(id),
  CONSTRAINT fk_dispute_case_after_sale FOREIGN KEY (after_sale_id) REFERENCES after_sale_request(id),
  CONSTRAINT fk_dispute_case_buyer FOREIGN KEY (buyer_id) REFERENCES user_account(id),
  CONSTRAINT fk_dispute_case_seller FOREIGN KEY (seller_id) REFERENCES user_account(id),
  CONSTRAINT fk_dispute_case_admin FOREIGN KEY (resolved_by) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易纠纷处理表';

CREATE TABLE dispute_action_log (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '纠纷处理日志ID',
  dispute_id BIGINT UNSIGNED NOT NULL COMMENT '纠纷单ID',
  action_by BIGINT UNSIGNED NOT NULL COMMENT '操作人用户ID',
  action_type ENUM('submit','append_evidence','status_change','admin_decision','close') NOT NULL COMMENT '操作类型',
  action_desc VARCHAR(1000) NOT NULL COMMENT '操作说明',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  KEY idx_dispute_action_dispute_time (dispute_id, created_at),
  CONSTRAINT fk_dispute_action_dispute FOREIGN KEY (dispute_id) REFERENCES dispute_case(id),
  CONSTRAINT fk_dispute_action_user FOREIGN KEY (action_by) REFERENCES user_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纠纷处理日志表';




-- ======================================================
-- 第七部分：交易域接口联调测试数据
-- 说明：用于订单相关接口（创建、列表、详情、取消、确认收货、状态日志）快速联调
-- 依赖用户：10001~10008（见前文 user_account 测试数据）
-- ======================================================

-- 1) 商品分类
INSERT INTO category (id, category_name, sort_no, is_enabled) VALUES
(2001, '手机数码', 1, 1),
(2002, '电脑办公', 2, 1),
(2003, '家居家具', 3, 1);

-- 2) 商品主数据（保证存在可下单 on_sale 商品）
INSERT INTO product (
  id, seller_id, category_id, title, subtitle, description, brand, model, condition_level,
  purchase_year, original_price, selling_price, can_bargain, trade_mode, pickup_city, pickup_address,
  stock, publish_status, view_count, favorite_count, published_at
) VALUES
(30001, 10003, 2001, '95新 iPhone 13 128G', '电池健康 88%', '无拆无修，功能正常，支持当面验机', 'Apple', 'iPhone 13', 'almost_new',
 2023, 5199.00, 2999.00, 1, 'both', '上海', '徐家汇地铁站2号口', 5, 'on_sale', 168, 23, '2026-04-01 10:00:00'),
(30002, 10003, 2002, '联想小新 Pro 14', '16G+512G', '学习办公足够，轻微使用痕迹', 'Lenovo', 'Pro14', 'good',
 2022, 5999.00, 2680.00, 1, 'shipping', '上海', '漕河泾开发区', 3, 'on_sale', 96, 11, '2026-04-02 14:20:00'),
(30003, 10004, 2001, '索尼 WH-1000XM4', '降噪耳机', '音质好，配件齐全', 'Sony', 'WH-1000XM4', 'good',
 2021, 2899.00, 1199.00, 1, 'both', '北京', '望京SOHO', 2, 'on_sale', 73, 8, '2026-04-03 09:30:00'),
(30004, 10004, 2003, '宜家书桌 120x60', '可自提', '桌面轻微划痕，结构稳固', 'IKEA', 'LAGKAPTEN', 'fair',
 2020, 699.00, 180.00, 1, 'pickup', '北京', '朝阳区青年路', 1, 'reserved', 41, 3, '2026-04-04 18:00:00');

-- 3) 商品图片（含封面）
INSERT INTO product_image (id, product_id, image_url, is_cover, sort_no) VALUES
(31001, 30001, '/uploads/products/30001_cover.jpg', 1, 1),
(31002, 30001, '/uploads/products/30001_2.jpg', 0, 2),
(31003, 30002, '/uploads/products/30002_cover.jpg', 1, 1),
(31004, 30003, '/uploads/products/30003_cover.jpg', 1, 1),
(31005, 30004, '/uploads/products/30004_cover.jpg', 1, 1);

-- 4) 订单主表（覆盖多状态，便于接口联调）
INSERT INTO trade_order (
  id, order_no, buyer_id, seller_id, order_status, trade_mode,
  total_amount, freight_amount, pay_amount, remark,
  receiver_name, receiver_phone, receiver_address, pickup_location,
  cancel_reason, paid_at, shipped_at, delivered_at, completed_at, cancelled_at,
  created_at, updated_at
) VALUES
(50001, 'OD202604120001', 10001, 10003, 'pending_payment', 'shipping',
 2999.00, 12.00, 3011.00, '工作日白天收货',
 '张三', '13800000001', '上海市徐汇区漕溪北路100号', NULL,
 NULL, NULL, NULL, NULL, NULL, NULL,
 '2026-04-10 10:20:00', '2026-04-10 10:20:00'),

(50002, 'OD202604120002', 10002, 10003, 'paid_pending_ship', 'pickup',
 2680.00, 0.00, 2680.00, '周末自提',
 NULL, NULL, NULL, '上海市徐汇区宜山路地铁站',
 NULL, '2026-04-10 12:40:00', NULL, NULL, NULL, NULL,
 '2026-04-10 12:30:00', '2026-04-10 12:40:00'),

(50003, 'OD202604120003', 10001, 10004, 'shipped', 'shipping',
 1199.00, 10.00, 1209.00, '请放前台',
 '张三', '13800000001', '上海市徐汇区虹桥路88号', NULL,
 NULL, '2026-04-09 09:35:00', '2026-04-09 15:10:00', NULL, NULL, NULL,
 '2026-04-09 09:20:00', '2026-04-09 15:10:00'),

(50004, 'OD202604120004', 10005, 10003, 'completed', 'pickup',
 2999.00, 0.00, 2999.00, '已现场验机',
 NULL, NULL, NULL, '上海市徐汇区徐家汇',
 NULL, '2026-04-08 11:05:00', '2026-04-08 13:00:00', '2026-04-08 16:20:00', '2026-04-08 17:10:00', NULL,
 '2026-04-08 10:50:00', '2026-04-08 17:10:00'),

(50005, 'OD202604120005', 10007, 10004, 'cancelled', 'shipping',
 180.00, 15.00, 195.00, '临时有事',
 '大刘', '13800000007', '北京市朝阳区青年路66号', NULL,
 '买家临时取消', NULL, NULL, NULL, NULL, '2026-04-11 09:20:00',
 '2026-04-11 09:00:00', '2026-04-11 09:20:00');

-- 5) 订单明细
INSERT INTO order_item (
  id, order_id, product_id, product_title, product_image_url, unit_price, quantity, subtotal_amount, created_at
) VALUES
(60001, 50001, 30001, '95新 iPhone 13 128G', '/uploads/products/30001_cover.jpg', 2999.00, 1, 2999.00, '2026-04-10 10:20:00'),
(60002, 50002, 30002, '联想小新 Pro 14', '/uploads/products/30002_cover.jpg', 2680.00, 1, 2680.00, '2026-04-10 12:30:00'),
(60003, 50003, 30003, '索尼 WH-1000XM4', '/uploads/products/30003_cover.jpg', 1199.00, 1, 1199.00, '2026-04-09 09:20:00'),
(60004, 50004, 30001, '95新 iPhone 13 128G', '/uploads/products/30001_cover.jpg', 2999.00, 1, 2999.00, '2026-04-08 10:50:00'),
(60005, 50005, 30004, '宜家书桌 120x60', '/uploads/products/30004_cover.jpg', 180.00, 1, 180.00, '2026-04-11 09:00:00');

-- 6) 支付单与支付流水（便于后续扩展联调）
INSERT INTO payment_order (
  id, order_id, payment_no, payment_channel, payment_status, payable_amount, paid_amount,
  channel_trade_no, paid_at, failed_reason, created_at, updated_at
) VALUES
(61001, 50001, 'PO202604120001', 'alipay', 'created', 3011.00, NULL, NULL, NULL, NULL, '2026-04-10 10:21:00', '2026-04-10 10:21:00'),
(61002, 50002, 'PO202604120002', 'wechat', 'paid', 2680.00, 2680.00, 'WX_TRADE_2680001', '2026-04-10 12:40:00', NULL, '2026-04-10 12:31:00', '2026-04-10 12:40:00'),
(61003, 50003, 'PO202604120003', 'balance', 'paid', 1209.00, 1209.00, 'BAL_TRADE_1209001', '2026-04-09 09:35:00', NULL, '2026-04-09 09:21:00', '2026-04-09 09:35:00'),
(61004, 50004, 'PO202604120004', 'alipay', 'paid', 2999.00, 2999.00, 'ALI_TRADE_2999001', '2026-04-08 11:05:00', NULL, '2026-04-08 10:51:00', '2026-04-08 11:05:00');

INSERT INTO payment_transaction (
  id, payment_order_id, transaction_type, transaction_status, amount, channel_trade_no, channel_response, occurred_at
) VALUES
(62001, 61002, 'pay', 'success', 2680.00, 'WX_TRADE_2680001', JSON_OBJECT('code', 'SUCCESS'), '2026-04-10 12:40:00'),
(62002, 61003, 'pay', 'success', 1209.00, 'BAL_TRADE_1209001', JSON_OBJECT('code', 'SUCCESS'), '2026-04-09 09:35:00'),
(62003, 61004, 'pay', 'success', 2999.00, 'ALI_TRADE_2999001', JSON_OBJECT('code', 'SUCCESS'), '2026-04-08 11:05:00');

-- 7) 发货信息与物流轨迹
INSERT INTO order_shipment (
  id, order_id, shipment_type, logistics_company, tracking_no, shipment_status,
  shipped_by, shipped_at, signed_at, pickup_code, pickup_verified_at, created_at, updated_at
) VALUES
(63001, 50003, 'shipping', '顺丰', 'SF202604090001', 'in_transit',
 10004, '2026-04-09 15:10:00', NULL, NULL, NULL, '2026-04-09 15:10:00', '2026-04-09 15:10:00'),
(63002, 50004, 'pickup', NULL, NULL, 'signed',
 10003, '2026-04-08 13:00:00', '2026-04-08 16:20:00', 'PK2026040801', '2026-04-08 16:20:00', '2026-04-08 13:00:00', '2026-04-08 16:20:00');

INSERT INTO logistics_trace (
  id, shipment_id, trace_time, trace_status, trace_detail, trace_location, created_at
) VALUES
(64001, 63001, '2026-04-09 16:00:00', '已揽收', '快件已由上海徐汇网点揽收', '上海市徐汇区', '2026-04-09 16:00:00'),
(64002, 63001, '2026-04-09 22:00:00', '运输中', '快件已发往上海转运中心', '上海市闵行区', '2026-04-09 22:00:00');

-- 8) 订单状态流转日志（用于 /orders/{id}/status-logs）
INSERT INTO order_status_log (
  id, order_id, from_status, to_status, changed_by, change_reason, changed_at
) VALUES
(70001, 50001, NULL, 'pending_payment', 10001, '创建订单', '2026-04-10 10:20:00'),

(70002, 50002, NULL, 'pending_payment', 10002, '创建订单', '2026-04-10 12:30:00'),
(70003, 50002, 'pending_payment', 'paid_pending_ship', 10002, '支付成功', '2026-04-10 12:40:00'),

(70004, 50003, NULL, 'pending_payment', 10001, '创建订单', '2026-04-09 09:20:00'),
(70005, 50003, 'pending_payment', 'paid_pending_ship', 10001, '支付成功', '2026-04-09 09:35:00'),
(70006, 50003, 'paid_pending_ship', 'shipped', 10004, '卖家发货', '2026-04-09 15:10:00'),

(70007, 50004, NULL, 'pending_payment', 10005, '创建订单', '2026-04-08 10:50:00'),
(70008, 50004, 'pending_payment', 'paid_pending_ship', 10005, '支付成功', '2026-04-08 11:05:00'),
(70009, 50004, 'paid_pending_ship', 'shipped', 10003, '卖家发货', '2026-04-08 13:00:00'),
(70010, 50004, 'shipped', 'delivered', 10005, '买家签收', '2026-04-08 16:20:00'),
(70011, 50004, 'delivered', 'completed', 10005, '买家确认收货', '2026-04-08 17:10:00'),

(70012, 50005, NULL, 'pending_payment', 10007, '创建订单', '2026-04-11 09:00:00'),
(70013, 50005, 'pending_payment', 'cancelled', 10007, '买家临时取消', '2026-04-11 09:20:00');









-- ======================================================
-- 第八部分：订单接口补充测试数据（幂等，可重复执行）
-- 用途：确保 openapi_trade_orders_v1.json 中接口可直接联调
-- ======================================================

-- A) 补充可用卖家账号（避免无卖家导致商品外键失败）
INSERT INTO user_account (
  id, username, nickname, phone, email, password_hash, can_buy, can_sell, is_admin, user_status
) VALUES
  (900001, 'seed_seller_1', '测试卖家1', '13990000001', 'seed_seller_1@example.com',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, 1, 0, 'active'),
  (900002, 'seed_seller_2', '测试卖家2', '13990000002', 'seed_seller_2@example.com',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, 1, 0, 'active')
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  can_sell = VALUES(can_sell),
  user_status = VALUES(user_status),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO user_profile (user_id, avatar_url, bio, credit_score, positive_rate, total_review_count)
VALUES
  (900001, '/avatars/seed_seller_1.jpg', '订单联调测试卖家1', 96, 100.00, 0),
  (900002, '/avatars/seed_seller_2.jpg', '订单联调测试卖家2', 94, 100.00, 0)
ON DUPLICATE KEY UPDATE
  bio = VALUES(bio),
  credit_score = VALUES(credit_score),
  positive_rate = VALUES(positive_rate),
  updated_at = CURRENT_TIMESTAMP;

-- B) 补充商品分类
INSERT INTO category (id, category_name, sort_no, is_enabled)
VALUES
  (910001, '联调-手机数码', 1, 1),
  (910002, '联调-电脑办公', 2, 1)
ON DUPLICATE KEY UPDATE
  category_name = VALUES(category_name),
  is_enabled = VALUES(is_enabled),
  updated_at = CURRENT_TIMESTAMP;

-- C) 补充可下单商品（关键：publish_status=on_sale 且 stock>0）
INSERT INTO product (
  id, seller_id, category_id, title, subtitle, description, brand, model, condition_level,
  purchase_year, original_price, selling_price, can_bargain, trade_mode,
  pickup_city, pickup_address, stock, publish_status, view_count, favorite_count, published_at
) VALUES
  (920001, 900001, 910001, '联调测试 iPhone 13 128G', '接口联调用样品', '用于订单接口创建/列表/详情测试',
   'Apple', 'iPhone 13', 'almost_new', 2023, 5199.00, 2999.00, 1, 'both',
   '上海', '徐家汇地铁站2号口', 20, 'on_sale', 0, 0, '2026-04-12 09:00:00'),
  (920002, 900001, 910002, '联调测试 ThinkPad X1', '接口联调用样品', '用于订单接口创建/取消测试',
   'Lenovo', 'X1 Carbon', 'good', 2022, 7999.00, 3499.00, 1, 'shipping',
   '上海', '漕河泾', 10, 'on_sale', 0, 0, '2026-04-12 09:10:00'),
  (920003, 900002, 910001, '联调测试 AirPods Pro', '接口联调用样品', '用于跨卖家场景测试',
   'Apple', 'AirPods Pro', 'good', 2023, 1999.00, 899.00, 1, 'both',
   '北京', '望京SOHO', 15, 'on_sale', 0, 0, '2026-04-12 09:20:00')
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  selling_price = VALUES(selling_price),
  stock = VALUES(stock),
  publish_status = VALUES(publish_status),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO product_image (id, product_id, image_url, is_cover, sort_no)
VALUES
  (930001, 920001, '/uploads/products/920001_cover.jpg', 1, 1),
  (930002, 920002, '/uploads/products/920002_cover.jpg', 1, 1),
  (930003, 920003, '/uploads/products/920003_cover.jpg', 1, 1)
ON DUPLICATE KEY UPDATE
  image_url = VALUES(image_url),
  is_cover = VALUES(is_cover),
  sort_no = VALUES(sort_no);

-- D) 补充已有订单（用于详情/列表/日志/确认收货/取消接口）
INSERT INTO trade_order (
  id, order_no, buyer_id, seller_id, order_status, trade_mode,
  total_amount, freight_amount, pay_amount, remark,
  receiver_name, receiver_phone, receiver_address, pickup_location,
  paid_at, shipped_at, delivered_at, completed_at, cancelled_at, cancel_reason,
  created_at, updated_at
) VALUES
  (940001, 'ODSEED202604120001', 10001, 900001, 'pending_payment', 'shipping',
   2999.00, 12.00, 3011.00, '联调-待支付订单',
   '张三', '13800000001', '上海市徐汇区测试路1号', NULL,
   NULL, NULL, NULL, NULL, NULL, NULL,
   '2026-04-12 10:00:00', '2026-04-12 10:00:00'),

  (940002, 'ODSEED202604120002', 10001, 900001, 'shipped', 'shipping',
   3499.00, 0.00, 3499.00, '联调-已发货订单',
   '张三', '13800000001', '上海市徐汇区测试路2号', NULL,
   '2026-04-12 10:10:00', '2026-04-12 11:00:00', NULL, NULL, NULL, NULL,
   '2026-04-12 10:05:00', '2026-04-12 11:00:00'),

  (940003, 'ODSEED202604120003', 10001, 900001, 'cancelled', 'pickup',
   2999.00, 0.00, 2999.00, '联调-已取消订单',
   NULL, NULL, NULL, '徐家汇地铁站2号口',
   NULL, NULL, NULL, NULL, '2026-04-12 12:00:00', '买家临时取消',
   '2026-04-12 11:30:00', '2026-04-12 12:00:00')
ON DUPLICATE KEY UPDATE
  order_status = VALUES(order_status),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO order_item (
  id, order_id, product_id, product_title, product_image_url, unit_price, quantity, subtotal_amount, created_at
) VALUES
  (950001, 940001, 920001, '联调测试 iPhone 13 128G', '/uploads/products/920001_cover.jpg', 2999.00, 1, 2999.00, '2026-04-12 10:00:00'),
  (950002, 940002, 920002, '联调测试 ThinkPad X1', '/uploads/products/920002_cover.jpg', 3499.00, 1, 3499.00, '2026-04-12 10:05:00'),
  (950003, 940003, 920001, '联调测试 iPhone 13 128G', '/uploads/products/920001_cover.jpg', 2999.00, 1, 2999.00, '2026-04-12 11:30:00')
ON DUPLICATE KEY UPDATE
  quantity = VALUES(quantity),
  subtotal_amount = VALUES(subtotal_amount);

INSERT INTO order_status_log (
  id, order_id, from_status, to_status, changed_by, change_reason, changed_at
) VALUES
  (960001, 940001, NULL, 'pending_payment', 10001, '创建订单', '2026-04-12 10:00:00'),
  (960002, 940002, NULL, 'pending_payment', 10001, '创建订单', '2026-04-12 10:05:00'),
  (960003, 940002, 'pending_payment', 'paid_pending_ship', 10001, '支付成功', '2026-04-12 10:10:00'),
  (960004, 940002, 'paid_pending_ship', 'shipped', 900001, '卖家发货', '2026-04-12 11:00:00'),
  (960005, 940003, NULL, 'pending_payment', 10001, '创建订单', '2026-04-12 11:30:00'),
  (960006, 940003, 'pending_payment', 'cancelled', 10001, '买家临时取消', '2026-04-12 12:00:00')
ON DUPLICATE KEY UPDATE
  to_status = VALUES(to_status),
  change_reason = VALUES(change_reason),
  changed_at = VALUES(changed_at);
