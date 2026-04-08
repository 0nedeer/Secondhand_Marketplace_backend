-- ======================================================
-- 论坛模块 + 基础依赖表
-- MySQL 8.4.6
-- 创建时间：2026-04-07
-- ======================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS secondhand_marketplace
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE secondhand_marketplace;

-- ======================================================
-- 第一部分：基础用户表（论坛必需）
-- ======================================================

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `role` ENUM('user', 'admin', 'super_admin') DEFAULT 'user' COMMENT '角色',
    `status` ENUM('active', 'banned', 'pending') DEFAULT 'active' COMMENT '状态：活跃/封禁/待审核',
    `is_muted` TINYINT(1) DEFAULT 0 COMMENT '是否禁言（0-否，1-是）',
    `mute_expire_at` DATETIME DEFAULT NULL COMMENT '禁言到期时间',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `id_card_no` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `id_card_front` VARCHAR(500) DEFAULT NULL COMMENT '身份证正面照URL',
    `id_card_back` VARCHAR(500) DEFAULT NULL COMMENT '身份证反面照URL',
    `real_name_status` ENUM('unverified', 'pending', 'verified', 'failed') DEFAULT 'unverified' COMMENT '实名认证状态',
    `credit_score` INT DEFAULT 100 COMMENT '信用分（0-100）',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(45) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    INDEX `idx_status` (`status`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

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
    `parent_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID（0表示顶级）',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0-禁用，1-启用）',
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
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0-禁用，1-启用）',
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
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览数',
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
    `parent_comment_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父评论ID（0表示顶级评论）',
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

-- 10. 论坛收藏表
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

-- 11. 论坛帖子转发表
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
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `role`, `status`, `real_name_status`)
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000000', 'admin@example.com', 'super_admin', 'active', 'verified');

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
    u.id AS author_id,
    u.username AS author_name,
    u.avatar AS author_avatar,
    u.credit_score AS author_credit_score,
    GROUP_CONCAT(DISTINCT t.tag_name SEPARATOR ',') AS tags,
    c.name AS category_name
FROM forum_post p
LEFT JOIN `user` u ON p.author_id = u.id
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
    u.username AS author_name,
    u.avatar AS author_avatar,
    u.credit_score AS author_credit_score,
    u.bio AS author_bio,
    u.created_at AS author_join_time,
    (SELECT COUNT(*) FROM forum_post WHERE author_id = p.author_id AND is_deleted = 0) AS author_post_count,
    (SELECT COUNT(*) FROM forum_follow_tag WHERE tag_id IN (SELECT tag_id FROM forum_post_tag WHERE post_id = p.id)) AS tag_follow_count
FROM forum_post p
LEFT JOIN `user` u ON p.author_id = u.id;

-- ======================================================
-- 第六部分：测试数据
-- ======================================================

-- 1. 插入测试用户（密码统一为：123456，实际已加密）
-- 注意：实际使用时密码需要正确加密，这里使用占位符
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `avatar`, `bio`, `role`, `status`, `credit_score`) VALUES
(10001, '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000001', 'zhangsan@example.com', '/avatars/zhangsan.jpg', '资深数码爱好者，诚信交易', 'user', 'active', 95),
(10002, '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000002', 'lisi@example.com', '/avatars/lisi.jpg', '学生党，求购二手教材', 'user', 'active', 88),
(10003, '王五', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000003', 'wangwu@example.com', '/avatars/wangwu.jpg', '职业卖家，童叟无欺', 'user', 'active', 92),
(10004, '赵六', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000004', 'zhaoliu@example.com', '/avatars/zhaoliu.jpg', '刚加入平台，多多关照', 'user', 'active', 75),
(10005, '小明', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000005', 'xiaoming@example.com', '/avatars/xiaoming.jpg', '喜欢淘二手好物', 'user', 'active', 85),
(10006, '小红', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000006', 'xiaohong@example.com', '/avatars/xiaohong.jpg', '专注母婴用品分享', 'user', 'banned', 60),
(10007, '大刘', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000007', 'daliu@example.com', '/avatars/daliu.jpg', '摄影爱好者，器材党', 'user', 'active', 98),
(10008, '小陈', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000008', 'xiaochen@example.com', '/avatars/xiaochen.jpg', '捡漏小能手', 'user', 'active', 82);

-- 2. 插入测试帖子
INSERT INTO `forum_post` (`id`, `author_id`, `category_id`, `post_type`, `title`, `content`, `audit_status`, `display_status`, `like_count`, `comment_count`, `share_count`, `collect_count`, `view_count`, `published_at`) VALUES
(10001, 10001, 2, 'review', 'iPhone 13 Pro 二手购买避坑指南', '最近在平台淘了一台iPhone 13 Pro，分享一下我的验机经验：\n\n1. 首先检查外观，看是否有拆修痕迹\n2. 用爱思助手查看电池健康度和是否全原装\n3. 测试面容ID、相机、扬声器等功能\n4. 查询序列号确认保修状态\n\n希望对大家有帮助！', 'approved', 'normal', 45, 12, 8, 23, 1560, '2026-03-20 10:30:00'),
(10002, 10002, 3, 'help', '想买二手笔记本电脑，预算3000以内有什么推荐？', '本人学生党，预算3000左右想买一台二手笔记本，主要用于写论文、看视频、偶尔用PS。请问各位大佬有什么推荐？需要注意什么？', 'approved', 'normal', 23, 18, 5, 9, 890, '2026-03-22 14:15:00'),
(10003, 10003, 1, 'sell', '9成新Switch OLED版出售', '今年3月购入，几乎没怎么玩，箱说全，带两个游戏（塞尔达+马里奥奥德赛）。\n\n成色：屏幕贴膜，机身无划痕\n价格：1800元可小刀\n交易方式：支持面交或邮寄\n\n有意者私聊', 'approved', 'normal', 67, 25, 15, 42, 2340, '2026-03-25 09:00:00'),
(10004, 10004, 4, 'normal', '在平台第一次卖东西就被骗了，大家小心', '事情是这样的：有人私聊我说要买我的旧手机，然后让我加微信，发了个假链接让我点...\n\n提醒各位新手卖家，任何让你点击外部链接的行为都要警惕！', 'approved', 'normal', 89, 32, 28, 56, 3420, '2026-03-18 20:45:00'),
(10005, 10005, 2, 'review', '二手Kindle到底值不值得买？', '最近入手了一台二手Kindle Paperwhite 4，价格300块。\n\n优点：\n- 墨水屏护眼\n- 续航给力\n- 价格便宜\n\n缺点：\n- 翻页慢\n- 不支持彩色\n\n建议：喜欢阅读的可以入手，追求体验的建议买新款。', 'approved', 'normal', 34, 9, 6, 18, 1200, '2026-03-28 16:20:00'),
(10006, 10006, 1, 'sell', '闲置婴儿推车，九成新', '宝宝长大了用不上了，好孩子品牌，可坐可躺，带遮阳棚。\n\n使用时间：约6个月\n成色：九成新，有正常使用痕迹\n价格：280元\n\n限自提，坐标深圳南山', 'pending', 'normal', 0, 0, 0, 0, 45, NULL),
(10007, 10007, 2, 'review', '二手相机购买经验分享，教你如何避坑', '玩摄影三年，前前后后买了5台二手相机，分享一下我的经验：\n\n1. 快门数不是唯一标准\n2. 一定要检查CMOS有没有坏点\n3. 镜头要对焦测试\n4. 最好面交，当场测试\n5. 保留聊天记录和交易凭证\n\n欢迎大家补充！', 'approved', 'featured', 156, 45, 32, 89, 5670, '2026-03-15 11:00:00'),
(10008, 10008, 3, 'help', '卖二手手机怎么定价？求指导', '手里有一台iPhone 12，256G，用了两年，电池健康82%，外观无磕碰。\n\n想问一下大概能卖多少钱？在哪里看参考价格比较准？', 'approved', 'normal', 12, 8, 2, 5, 567, '2026-03-30 08:30:00'),
(10009, 10001, 4, 'normal', '聊聊平台最近的变化，越来越好用了', '用了这个平台一年多了，最近发现几个改进：\n\n- 聊天功能更稳定了\n- 审核速度快了很多\n- 增加了AI助手功能\n\n希望平台越来越好！', 'approved', 'normal', 28, 6, 3, 11, 890, '2026-03-29 19:00:00'),
(10010, 10003, 1, 'sell', '搬家甩卖：宜家书桌+椅子', '因搬家需要清空，宜家购买的书桌和椅子，使用一年。\n\n书桌尺寸：120*60cm\n成色：八成新\n价格：书桌150元，椅子80元，一起200元\n\n需要自提，坐标上海徐汇', 'approved', 'top', 34, 15, 7, 28, 1560, '2026-03-27 13:30:00');

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
(10002, 10001, 10001, 10002, 10003, '爱思助手可以改数据，建议用沙漏验机更准', 'approved', 12, 1),
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