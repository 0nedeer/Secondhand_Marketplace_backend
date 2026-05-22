-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: secondhand_marketplace
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_log`
--

DROP TABLE IF EXISTS `admin_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_id` bigint unsigned NOT NULL COMMENT '管理员ID',
  `target_type` enum('user','post','comment','tag') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作目标类型',
  `target_id` bigint unsigned DEFAULT NULL COMMENT '操作目标ID',
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型（ban_user, delete_post, approve_post等）',
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作原因',
  `before_data` json DEFAULT NULL COMMENT '操作前数据快照',
  `after_data` json DEFAULT NULL COMMENT '操作后数据快照',
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_log`
--

LOCK TABLES `admin_log` WRITE;
/*!40000 ALTER TABLE `admin_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `after_sale_evidence`
--

DROP TABLE IF EXISTS `after_sale_evidence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `after_sale_evidence` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '售后凭证ID',
  `after_sale_id` bigint unsigned NOT NULL COMMENT '售后申请ID',
  `evidence_type` enum('image','video','text','logistics_doc') NOT NULL COMMENT '凭证类型',
  `content_url` varchar(500) DEFAULT NULL COMMENT '凭证文件URL',
  `content_text` varchar(1000) DEFAULT NULL COMMENT '文本说明',
  `uploaded_by` bigint unsigned NOT NULL COMMENT '上传用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_after_sale_evidence_after_sale` (`after_sale_id`),
  KEY `fk_after_sale_evidence_user` (`uploaded_by`),
  CONSTRAINT `fk_after_sale_evidence_after_sale` FOREIGN KEY (`after_sale_id`) REFERENCES `after_sale_request` (`id`),
  CONSTRAINT `fk_after_sale_evidence_user` FOREIGN KEY (`uploaded_by`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='售后凭证表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `after_sale_evidence`
--

LOCK TABLES `after_sale_evidence` WRITE;
/*!40000 ALTER TABLE `after_sale_evidence` DISABLE KEYS */;
INSERT INTO `after_sale_evidence` VALUES (1,1,'image','/uploads/after_sales/as_90001_1.jpg','划痕位置近照',10009,'2026-04-13 13:24:04');
/*!40000 ALTER TABLE `after_sale_evidence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `after_sale_request`
--

DROP TABLE IF EXISTS `after_sale_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `after_sale_request` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '售后申请ID',
  `after_sale_no` varchar(64) NOT NULL COMMENT '售后单号（业务唯一）',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_item_id` bigint unsigned NOT NULL COMMENT '订单明细ID',
  `buyer_id` bigint unsigned NOT NULL COMMENT '买家ID（申请人）',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家ID',
  `request_type` enum('return_refund','refund_only','exchange','complaint') NOT NULL COMMENT '售后类型',
  `request_reason` varchar(255) NOT NULL COMMENT '申请原因',
  `detail_desc` varchar(1000) DEFAULT NULL COMMENT '问题描述',
  `requested_amount` decimal(10,2) DEFAULT NULL COMMENT '申请退款金额（元）',
  `final_amount` decimal(10,2) DEFAULT NULL COMMENT '最终退款金额（元）',
  `request_status` enum('pending_seller','pending_admin','approved','rejected','cancelled','completed') NOT NULL DEFAULT 'pending_seller' COMMENT '售后状态',
  `seller_response` varchar(1000) DEFAULT NULL COMMENT '卖家处理意见',
  `seller_responded_at` datetime DEFAULT NULL COMMENT '卖家响应时间',
  `admin_id` bigint unsigned DEFAULT NULL COMMENT '处理管理员ID',
  `admin_decision` varchar(1000) DEFAULT NULL COMMENT '管理员裁决意见',
  `closed_at` datetime DEFAULT NULL COMMENT '关闭时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_after_sale_no` (`after_sale_no`),
  KEY `idx_after_sale_order` (`order_id`),
  KEY `idx_after_sale_buyer` (`buyer_id`,`created_at`),
  KEY `idx_after_sale_seller` (`seller_id`,`created_at`),
  KEY `idx_after_sale_status` (`request_status`),
  KEY `fk_after_sale_order_item` (`order_item_id`),
  KEY `fk_after_sale_admin` (`admin_id`),
  CONSTRAINT `fk_after_sale_admin` FOREIGN KEY (`admin_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_after_sale_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_after_sale_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_after_sale_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`),
  CONSTRAINT `fk_after_sale_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `chk_after_sale_amount` CHECK (((`requested_amount` is null) or (`requested_amount` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='售后申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `after_sale_request`
--

LOCK TABLES `after_sale_request` WRITE;
/*!40000 ALTER TABLE `after_sale_request` DISABLE KEYS */;
INSERT INTO `after_sale_request` VALUES (1,'AS202604131322065864',940009,950009,10009,900002,'refund_only','商品存在描述外划痕','开箱后发现侧边有明显划痕',100.00,100.00,'approved','同意转交平台介入处理。','2026-04-13 13:25:59',900003,'证据充分，进入退款处理',NULL,'2026-04-13 13:22:06','2026-04-13 13:22:06'),(2,'AS202604131327451284',940006,950006,10009,900002,'refund_only','巴拉巴拉巴拉','jiwajiwajiwajiwa',666.00,NULL,'cancelled',NULL,NULL,NULL,'与卖家协商后已解决','2026-04-13 13:27:55','2026-04-13 13:27:45','2026-04-13 13:27:45');
/*!40000 ALTER TABLE `after_sale_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '排序号（越小越靠前）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否1是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=910003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表（单层基础分类）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (910001,'手机数码',1,1,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(910002,'电脑办公',2,1,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(910003,'图书教材',3,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910004,'运动户外',4,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910005,'家居日用',5,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910006,'服饰鞋包',6,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910007,'家用电器',7,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910008,'美妆个护',8,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910009,'乐器文玩',9,1,'2026-04-14 09:00:00','2026-04-14 09:00:00'),(910010,'母婴用品',10,1,'2026-04-14 09:00:00','2026-04-14 09:00:00');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispute_action_log`
--

DROP TABLE IF EXISTS `dispute_action_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dispute_action_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '纠纷处理日志ID',
  `dispute_id` bigint unsigned NOT NULL COMMENT '纠纷单ID',
  `action_by` bigint unsigned NOT NULL COMMENT '操作人用户ID',
  `action_type` enum('submit','append_evidence','status_change','admin_decision','close') NOT NULL COMMENT '操作类型',
  `action_desc` varchar(1000) NOT NULL COMMENT '操作说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_dispute_action_dispute_time` (`dispute_id`,`created_at`),
  KEY `fk_dispute_action_user` (`action_by`),
  CONSTRAINT `fk_dispute_action_dispute` FOREIGN KEY (`dispute_id`) REFERENCES `dispute_case` (`id`),
  CONSTRAINT `fk_dispute_action_user` FOREIGN KEY (`action_by`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='纠纷处理日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispute_action_log`
--

LOCK TABLES `dispute_action_log` WRITE;
/*!40000 ALTER TABLE `dispute_action_log` DISABLE KEYS */;
INSERT INTO `dispute_action_log` VALUES (2,2,10009,'submit','双方对退款金额存在争议，申请平台介入。','2026-04-13 13:39:00'),(3,2,10009,'status_change','补充沟通记录，等待更多证据','2026-04-13 13:40:15'),(4,2,900003,'admin_decision','卖家承担主要责任，支持部分退款。','2026-04-13 13:42:25');
/*!40000 ALTER TABLE `dispute_action_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispute_case`
--

DROP TABLE IF EXISTS `dispute_case`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dispute_case` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '纠纷单ID',
  `dispute_no` varchar(64) NOT NULL COMMENT '纠纷单号（业务唯一）',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `after_sale_id` bigint unsigned DEFAULT NULL COMMENT '关联售后ID',
  `buyer_id` bigint unsigned NOT NULL COMMENT '买家ID',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家ID',
  `current_status` enum('open','investigating','waiting_evidence','resolved','closed') NOT NULL DEFAULT 'open' COMMENT '纠纷状态',
  `responsibility` enum('buyer','seller','both','platform','undetermined') NOT NULL DEFAULT 'undetermined' COMMENT '责任判定',
  `resolution_result` varchar(1000) DEFAULT NULL COMMENT '处理结果',
  `resolution_amount` decimal(10,2) DEFAULT NULL COMMENT '裁定退款金额（元）',
  `resolved_by` bigint unsigned DEFAULT NULL COMMENT '处理管理员ID',
  `resolved_at` datetime DEFAULT NULL COMMENT '处理完成时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dispute_case_no` (`dispute_no`),
  KEY `idx_dispute_case_order` (`order_id`),
  KEY `idx_dispute_case_status` (`current_status`),
  KEY `fk_dispute_case_after_sale` (`after_sale_id`),
  KEY `fk_dispute_case_buyer` (`buyer_id`),
  KEY `fk_dispute_case_seller` (`seller_id`),
  KEY `fk_dispute_case_admin` (`resolved_by`),
  CONSTRAINT `fk_dispute_case_admin` FOREIGN KEY (`resolved_by`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_dispute_case_after_sale` FOREIGN KEY (`after_sale_id`) REFERENCES `after_sale_request` (`id`),
  CONSTRAINT `fk_dispute_case_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_dispute_case_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_dispute_case_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易纠纷处理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispute_case`
--

LOCK TABLES `dispute_case` WRITE;
/*!40000 ALTER TABLE `dispute_case` DISABLE KEYS */;
INSERT INTO `dispute_case` VALUES (2,'DP202604131339009626',940009,1,10009,900002,'resolved','seller','卖家承担主要责任，支持部分退款。',100.00,900003,'2026-04-13 13:42:25','2026-04-13 13:39:00','2026-04-13 13:39:00');
/*!40000 ALTER TABLE `dispute_case` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `escrow_record`
--

DROP TABLE IF EXISTS `escrow_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `escrow_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '担保资金记录ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `escrow_status` enum('frozen','released_to_seller','refund_to_buyer') NOT NULL DEFAULT 'frozen' COMMENT '担保状态',
  `frozen_amount` decimal(10,2) NOT NULL COMMENT '冻结金额（元）',
  `released_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已释放金额（元）',
  `refunded_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款金额（元）',
  `frozen_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '冻结时间',
  `released_at` datetime DEFAULT NULL COMMENT '解冻到账时间',
  `refunded_at` datetime DEFAULT NULL COMMENT '退款完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_escrow_record_order` (`order_id`),
  CONSTRAINT `fk_escrow_record_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `chk_escrow_record_amount` CHECK (((`frozen_amount` >= 0) and (`released_amount` >= 0) and (`refunded_amount` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='担保资金表（买家确认前平台暂存）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `escrow_record`
--

LOCK TABLES `escrow_record` WRITE;
/*!40000 ALTER TABLE `escrow_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `escrow_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_audit_log`
--

DROP TABLE IF EXISTS `forum_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_audit_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `target_type` enum('post','comment') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核目标类型',
  `target_id` bigint unsigned NOT NULL COMMENT '目标ID',
  `auditor_id` bigint unsigned NOT NULL COMMENT '审核员ID',
  `action` enum('approve','reject','hide','delete','restore') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核操作',
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核原因/驳回原因',
  `old_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '旧状态',
  `new_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '新状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛审核日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_audit_log`
--

LOCK TABLES `forum_audit_log` WRITE;
/*!40000 ALTER TABLE `forum_audit_log` DISABLE KEYS */;
INSERT INTO `forum_audit_log` VALUES (1,'post',10006,1,'reject','商品图片不清晰，请重新上传','pending','rejected','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_category`
--

DROP TABLE IF EXISTS `forum_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID，0表示顶级',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序序号',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用，0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_category`
--

LOCK TABLES `forum_category` WRITE;
/*!40000 ALTER TABLE `forum_category` DISABLE KEYS */;
INSERT INTO `forum_category` VALUES (1,0,'二手交易','category_trade',1,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(2,0,'经验分享','category_share',2,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(3,0,'求助问答','category_help',3,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(4,0,'闲聊灌水','category_chat',4,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(5,1,'手机数码',NULL,1,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(6,1,'家居日用',NULL,2,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(7,1,'服饰鞋包',NULL,3,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(8,1,'母婴育儿',NULL,4,1,'2026-04-12 22:18:33','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_collect`
--

DROP TABLE IF EXISTS `forum_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_collect` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`,`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_collect`
--

LOCK TABLES `forum_collect` WRITE;
/*!40000 ALTER TABLE `forum_collect` DISABLE KEYS */;
INSERT INTO `forum_collect` VALUES (1,10001,10007,'2026-04-12 22:18:33'),(2,10002,10007,'2026-04-12 22:18:33'),(3,10003,10007,'2026-04-12 22:18:33'),(4,10002,10001,'2026-04-12 22:18:33'),(5,10005,10001,'2026-04-12 22:18:33'),(6,10001,10003,'2026-04-12 22:18:33'),(7,10007,10003,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_comment`
--

DROP TABLE IF EXISTS `forum_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `parent_comment_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父评论ID，0表示顶级评论',
  `reply_to_user_id` bigint unsigned DEFAULT NULL COMMENT '回复的目标用户ID',
  `commenter_id` bigint unsigned NOT NULL COMMENT '评论用户ID',
  `content` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  `audit_status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `reply_count` int NOT NULL DEFAULT '0' COMMENT '回复数（仅顶级评论使用）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_comment_id` (`parent_comment_id`),
  KEY `idx_commenter_id` (`commenter_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=10016 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_comment`
--

LOCK TABLES `forum_comment` WRITE;
/*!40000 ALTER TABLE `forum_comment` DISABLE KEYS */;
INSERT INTO `forum_comment` VALUES (10001,10001,0,NULL,10002,'感谢分享，很实用！正准备买二手iPhone',0,'approved',10,2,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,10001,10001,10002,10003,'爱助手可以改数据，建议用沙漏验机更准',0,'approved',12,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,10001,10002,10003,10001,'谢谢提醒，沙漏确实更靠谱',0,'approved',3,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,10001,0,NULL,10007,'写得很好，已收藏',0,'approved',5,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,10002,0,NULL,10001,'推荐ThinkPad X1 Carbon 2018款，3000左右能拿下',0,'approved',17,3,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,10002,10005,10001,10002,'这个型号好用吗？散热怎么样？',0,'approved',2,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,10002,10006,10002,10001,'散热还不错，办公完全够用',0,'approved',1,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,10002,0,NULL,10007,'建议加点预算上M1 MacBook Air',0,'approved',8,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,10003,0,NULL,10001,'价格还能优惠吗？坐标哪里？',0,'approved',3,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10010,10003,10009,10001,10003,'可以小刀，坐标北京朝阳',0,'approved',2,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10011,10004,0,NULL,10001,'感谢提醒，骗子太可恶了',0,'approved',15,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10012,10004,0,NULL,10007,'建议平台完善防骗机制',0,'approved',8,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10013,10007,0,NULL,10001,'大佬写得太好了！已收藏+转发',0,'approved',12,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10014,10007,10013,10001,10002,'谢谢支持，有问题可以问我',0,'approved',3,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10015,10010,0,NULL,10002,'书桌还在吗？我周末可以自提',0,'approved',2,1,'2026-04-12 22:18:33','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_follow_tag`
--

DROP TABLE IF EXISTS `forum_follow_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_follow_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`,`tag_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_follow_tag`
--

LOCK TABLES `forum_follow_tag` WRITE;
/*!40000 ALTER TABLE `forum_follow_tag` DISABLE KEYS */;
INSERT INTO `forum_follow_tag` VALUES (1,10001,1,'2026-04-12 22:18:33'),(2,10001,2,'2026-04-12 22:18:33'),(3,10001,5,'2026-04-12 22:18:33'),(4,10002,1,'2026-04-12 22:18:33'),(5,10002,3,'2026-04-12 22:18:33'),(6,10003,1,'2026-04-12 22:18:33'),(7,10003,7,'2026-04-12 22:18:33'),(8,10007,1,'2026-04-12 22:18:33'),(9,10007,2,'2026-04-12 22:18:33'),(10,10007,5,'2026-04-12 22:18:33'),(11,10007,8,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_follow_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post`
--

DROP TABLE IF EXISTS `forum_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `author_id` bigint unsigned NOT NULL COMMENT '发帖用户ID',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '分类ID',
  `post_type` enum('normal','help','sell','review') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'normal' COMMENT '帖子类型',
  `product_id` bigint unsigned DEFAULT NULL COMMENT '关联商品ID（售卖帖必填）',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子标题',
  `content` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子正文',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  `audit_status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  `display_status` enum('normal','hidden','featured','top') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'normal' COMMENT '展示状态',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `share_count` int NOT NULL DEFAULT '0' COMMENT '转发数',
  `collect_count` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `last_commented_at` datetime DEFAULT NULL COMMENT '最后评论时间',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `reject_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核驳回原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_display_status` (`display_status`),
  KEY `idx_published_at` (`published_at`),
  KEY `idx_last_commented_at` (`last_commented_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  FULLTEXT KEY `idx_ft_title_content` (`title`,`content`)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post`
--

LOCK TABLES `forum_post` WRITE;
/*!40000 ALTER TABLE `forum_post` DISABLE KEYS */;
INSERT INTO `forum_post` VALUES (10001,10001,2,'review',NULL,'iPhone 13 Pro 二手购买避坑指南','最近在平台淘了一台iPhone 13 Pro，分享一下我的验机经验：\n\n1. 首先检查外观，看是否有拆修痕迹\n2. 用爱思助手查看电池健康度和是否全原装\n3. 测试面容ID、相机扬声器等功能\n4. 查询序列号确认保修状态\n\n希望对大家有帮助。',0,'approved','normal',48,4,8,23,1560,'2026-04-12 22:18:33','2026-03-20 10:30:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,10002,3,'help',NULL,'想买二手笔记本电脑，预算3000以内有什么推荐？','本人学生党，预算3000左右想买一台二手笔记本，主要用于写论文、看视频、偶尔用PS。请问各位大佬有什么推荐？需要注意什么？',0,'approved','normal',23,4,5,9,890,'2026-04-12 22:18:33','2026-03-22 14:15:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,10003,1,'sell',NULL,'9成新Switch OLED版出售','今年3月购入，几乎没么玩，箱说全，带两个游戏（塞尔达+马里奥奥德赛）\n\n成色：屏幕贴膜，机身无划痕\n价格：2800元可小刀\n交易方式：支持面交或邮寄\n\n有意者私聊',0,'approved','normal',71,2,15,42,2340,'2026-04-12 22:18:33','2026-03-25 09:00:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,10004,4,'normal',NULL,'在平台第一次卖东西就被骗了，大家小心','事情是这样的：有人私聊我说要买我的旧手机，然后让我加微信，发了个假链接让我点...\n\n提醒各位新手卖家，任何让你点击外部链接的行为都要警惕。',0,'approved','normal',89,2,28,56,3420,'2026-04-12 22:18:33','2026-03-18 20:45:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,10005,2,'review',NULL,'二手Kindle到底值不值得买？','最近入手了一台二手Kindle Paperwhite 4，价格900块\n\n优点：\n- 墨水屏护眼\n- 续航给力\n- 价格便宜\n\n缺点：\n- 翻页慢\n- 不支持彩色\n\n建议：喜欢阅读的可以入手，追求体验的建议买新款。',0,'approved','normal',34,9,6,18,1200,NULL,'2026-03-28 16:20:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,10006,1,'sell',NULL,'闲置婴儿推车，九成新','宝宝长大了用不上了，好孩子品牌，可坐可躺，带遮阳棚\n\n使用时间：约6个月\n成色：九成新，有正常使用痕迹\n价格：80元\n\n限自提，坐标深圳南山',0,'pending','normal',0,0,0,0,45,NULL,NULL,NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,10007,2,'review',NULL,'二手相机购买经验分享，教你如何避坑','玩摄影三年，前前后后买了5台二手相机，分享一下我的经验：\n\n1. 快门数不是唯一标准\n2. 一定要检查CMOS有没有坏点\n3. 镜头要对焦测试\n4. 最好面交，当场测试\n5. 保留聊天记录和交易凭证\n\n欢迎大家补充。',0,'approved','featured',159,2,32,89,5670,'2026-04-12 22:18:33','2026-03-15 11:00:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,10008,3,'help',NULL,'卖二手手机怎么定价？求指导','手里有一台iPhone 12 256G，用了两年，电池健康82%，外观无磕碰。\n\n想问一下大概能卖多少钱？在哪里看参考价格比较准？',0,'approved','normal',12,8,2,5,567,NULL,'2026-03-30 08:30:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,10001,4,'normal',NULL,'聊聊平台最近的变化，越来越好用了','用了这个平台一年多了，最近发现几个改进：\n\n- 聊天功能更稳定了\n- 审核速度快了很多\n- 增加了AI助手功能\n\n希望平台越来越好。',0,'approved','normal',28,6,3,11,890,NULL,'2026-03-29 19:00:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10010,10003,1,'sell',NULL,'搬家甩卖：宜家书桌+椅子','因搬家需要清空，宜家购买的书桌和椅子，使用一年\n\n书桌尺寸：120*60cm\n成色：八成新\n价格：书桌150元，椅子80元，一起200元\n\n需要自提，坐标上海徐汇',0,'approved','top',34,1,7,28,1560,'2026-04-12 22:18:33','2026-03-27 13:30:00',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post_media`
--

DROP TABLE IF EXISTS `forum_post_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post_media` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '媒体ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `media_type` enum('image','video') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '媒体类型',
  `media_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '媒体URL',
  `cover_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频封面URL',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子媒体附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post_media`
--

LOCK TABLES `forum_post_media` WRITE;
/*!40000 ALTER TABLE `forum_post_media` DISABLE KEYS */;
INSERT INTO `forum_post_media` VALUES (1,10001,'image','/uploads/posts/10001_1.jpg',NULL,1,'2026-04-12 22:18:33'),(2,10001,'image','/uploads/posts/10001_2.jpg',NULL,2,'2026-04-12 22:18:33'),(3,10003,'image','/uploads/posts/10003_1.jpg',NULL,1,'2026-04-12 22:18:33'),(4,10003,'image','/uploads/posts/10003_2.jpg',NULL,2,'2026-04-12 22:18:33'),(5,10003,'image','/uploads/posts/10003_3.jpg',NULL,3,'2026-04-12 22:18:33'),(6,10005,'image','/uploads/posts/10005_1.jpg',NULL,1,'2026-04-12 22:18:33'),(7,10007,'image','/uploads/posts/10007_1.jpg',NULL,1,'2026-04-12 22:18:33'),(8,10007,'image','/uploads/posts/10007_2.jpg',NULL,2,'2026-04-12 22:18:33'),(9,10010,'image','/uploads/posts/10010_1.jpg',NULL,1,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_post_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post_share`
--

DROP TABLE IF EXISTS `forum_post_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post_share` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '转发记录ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `user_id` bigint unsigned NOT NULL COMMENT '转发用户ID',
  `share_channel` enum('in_app','wechat','qq','weibo','copy_link') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '转发渠道',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '转发时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子转发表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post_share`
--

LOCK TABLES `forum_post_share` WRITE;
/*!40000 ALTER TABLE `forum_post_share` DISABLE KEYS */;
INSERT INTO `forum_post_share` VALUES (1,10007,10001,'wechat','2026-04-12 22:18:33'),(2,10007,10002,'qq','2026-04-12 22:18:33'),(3,10007,10003,'weibo','2026-04-12 22:18:33'),(4,10001,10005,'in_app','2026-04-12 22:18:33'),(5,10003,10001,'wechat','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_post_share` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post_tag`
--

DROP TABLE IF EXISTS `forum_post_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_tag` (`post_id`,`tag_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post_tag`
--

LOCK TABLES `forum_post_tag` WRITE;
/*!40000 ALTER TABLE `forum_post_tag` DISABLE KEYS */;
INSERT INTO `forum_post_tag` VALUES (1,10001,1,'2026-04-12 22:18:33'),(2,10001,5,'2026-04-12 22:18:33'),(3,10001,8,'2026-04-12 22:18:33'),(4,10002,3,'2026-04-12 22:18:33'),(5,10002,7,'2026-04-12 22:18:33'),(6,10003,1,'2026-04-12 22:18:33'),(7,10003,7,'2026-04-12 22:18:33'),(8,10004,6,'2026-04-12 22:18:33'),(9,10004,1,'2026-04-12 22:18:33'),(10,10005,5,'2026-04-12 22:18:33'),(11,10005,8,'2026-04-12 22:18:33'),(12,10006,1,'2026-04-12 22:18:33'),(13,10006,4,'2026-04-12 22:18:33'),(14,10007,1,'2026-04-12 22:18:33'),(15,10007,2,'2026-04-12 22:18:33'),(16,10007,5,'2026-04-12 22:18:33'),(17,10008,3,'2026-04-12 22:18:33'),(18,10008,7,'2026-04-12 22:18:33'),(19,10009,1,'2026-04-12 22:18:33'),(20,10009,4,'2026-04-12 22:18:33'),(21,10010,1,'2026-04-12 22:18:33'),(22,10010,4,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_post_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post_view_daily`
--

DROP TABLE IF EXISTS `forum_post_view_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post_view_daily` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '浏览统计ID',
  `post_id` bigint unsigned NOT NULL COMMENT '帖子ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `uv_count` int NOT NULL DEFAULT '0' COMMENT '独立访客数',
  `pv_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_date` (`post_id`,`stat_date`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子日浏览统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post_view_daily`
--

LOCK TABLES `forum_post_view_daily` WRITE;
/*!40000 ALTER TABLE `forum_post_view_daily` DISABLE KEYS */;
INSERT INTO `forum_post_view_daily` VALUES (1,10001,'2026-03-28',120,345,'2026-04-12 22:18:33'),(2,10001,'2026-03-29',89,234,'2026-04-12 22:18:33'),(3,10003,'2026-03-28',156,456,'2026-04-12 22:18:33'),(4,10007,'2026-03-28',234,678,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_post_view_daily` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_reaction`
--

DROP TABLE IF EXISTS `forum_reaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_reaction` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '互动ID',
  `target_type` enum('post','comment') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '互动目标类型',
  `target_id` bigint unsigned NOT NULL COMMENT '目标ID',
  `user_id` bigint unsigned NOT NULL COMMENT '操作用户ID',
  `reaction_type` enum('like','dislike') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'like' COMMENT '互动类型',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_target_user` (`target_type`,`target_id`,`user_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛互动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_reaction`
--

LOCK TABLES `forum_reaction` WRITE;
/*!40000 ALTER TABLE `forum_reaction` DISABLE KEYS */;
INSERT INTO `forum_reaction` VALUES (1,'post',10001,10002,'like','2026-04-12 22:18:33'),(2,'post',10001,10003,'like','2026-04-12 22:18:33'),(3,'post',10001,10005,'like','2026-04-12 22:18:33'),(4,'post',10003,10001,'like','2026-04-12 22:18:33'),(5,'post',10003,10002,'like','2026-04-12 22:18:33'),(6,'post',10003,10004,'like','2026-04-12 22:18:33'),(7,'post',10003,10007,'like','2026-04-12 22:18:33'),(8,'post',10007,10001,'like','2026-04-12 22:18:33'),(9,'post',10007,10002,'like','2026-04-12 22:18:33'),(10,'post',10007,10003,'like','2026-04-12 22:18:33'),(11,'comment',10001,10003,'like','2026-04-12 22:18:33'),(12,'comment',10001,10005,'like','2026-04-12 22:18:33'),(13,'comment',10005,10002,'like','2026-04-12 22:18:33'),(14,'comment',10005,10007,'like','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_reaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_report`
--

DROP TABLE IF EXISTS `forum_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_report` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `target_type` enum('post','comment') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报目标类型',
  `target_id` bigint unsigned NOT NULL COMMENT '目标ID',
  `reporter_id` bigint unsigned NOT NULL COMMENT '举报人ID',
  `report_reason` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '举报原因（违规内容/广告/欺诈等）',
  `report_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举报详细描述',
  `evidence_urls` text COLLATE utf8mb4_unicode_ci COMMENT '证据图片URL列表（JSON格式）',
  `report_status` enum('pending','processing','resolved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '处理状态',
  `handled_by` bigint unsigned DEFAULT NULL COMMENT '处理管理员ID',
  `handle_result` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理结果说明',
  `handled_at` datetime DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_report_status` (`report_status`),
  KEY `idx_handled_by` (`handled_by`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_report`
--

LOCK TABLES `forum_report` WRITE;
/*!40000 ALTER TABLE `forum_report` DISABLE KEYS */;
INSERT INTO `forum_report` VALUES (1,'post',10004,10002,'广告','帖子内容含有外部链接，疑似引流',NULL,'resolved',1,'已处理，帖子正常，无违规','2026-03-19 10:00:00','2026-04-12 22:18:33'),(2,'post',10006,10003,'欺诈','描述与实物不符的嫌疑',NULL,'pending',NULL,NULL,NULL,'2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_tag`
--

DROP TABLE IF EXISTS `forum_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签图标URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序序号',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用，0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`),
  KEY `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_tag`
--

LOCK TABLES `forum_tag` WRITE;
/*!40000 ALTER TABLE `forum_tag` DISABLE KEYS */;
INSERT INTO `forum_tag` VALUES (1,'热门','hot',1,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(2,'精华','essence',2,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(3,'求助','help',3,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(4,'已解决','solved',4,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(5,'干货','dry_goods',5,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(6,'避坑','avoid_pit',6,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(7,'砍价','bargain',7,1,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(8,'验机','check',8,1,'2026-04-12 22:18:33','2026-04-12 22:18:33');
/*!40000 ALTER TABLE `forum_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Additional realistic forum seed data for admin/forum testing
--

LOCK TABLES `forum_post` WRITE;
/*!40000 ALTER TABLE `forum_post` DISABLE KEYS */;
INSERT INTO `forum_post` VALUES
(10011,10010,5,'sell',NULL,'出自用iPad Air 5，64G蓝色，适合记笔记','去年开学买的，主要用GoodNotes和看网课，屏幕一直贴膜，边框有一处很浅的小磕碰。\n\n配置：iPad Air 5 64G WiFi 蓝色\n配件：原盒、充电器、类纸膜、保护壳\n价格：2850元，可小刀\n交易：深圳大学城附近面交优先，可当场登录检查。',0,'approved','normal',26,3,4,12,780,'2026-04-14 10:42:00','2026-04-14 09:15:00',NULL,'2026-04-14 09:05:00','2026-04-14 10:42:00'),
(10012,10011,6,'sell',NULL,'毕业搬宿舍，转让折叠桌和收纳架','宿舍要清空，折叠桌用了一个学期，收纳架无变形。\n\n折叠桌：80*50cm，45元\n三层收纳架：35元\n一起带走70元。\n\n成都锦江校区东门自提，晚上7点后方便。',0,'approved','normal',14,2,1,7,420,'2026-04-14 11:08:00','2026-04-14 09:45:00',NULL,'2026-04-14 09:40:00','2026-04-14 11:08:00'),
(10013,10012,2,'review',NULL,'二手降噪耳机怎么验？我的QC45入手记录','上周收了一副Bose QC45，简单记录一下验货流程。\n\n1. 看耳罩和头梁是否老化\n2. 连App查看固件和电量\n3. 地铁口测试降噪是否偏音\n4. 麦克风录音听底噪\n5. 查序列号和购买凭证\n\n最后以980元成交，个人觉得比盲买稳很多。',0,'approved','featured',67,5,9,31,1880,'2026-04-14 14:20:00','2026-04-14 10:20:00',NULL,'2026-04-14 10:10:00','2026-04-14 14:20:00'),
(10014,10013,3,'help',NULL,'求问婴儿安全座椅二手能不能买？','家里临时需要一个安全座椅，新的预算有点高。二手安全座椅有哪些一定不能碰的情况？比如事故车拆下来的、年限太久的怎么判断？希望有经验的同学给点建议。',0,'approved','normal',19,4,2,10,650,'2026-04-14 12:30:00','2026-04-14 10:55:00',NULL,'2026-04-14 10:50:00','2026-04-14 12:30:00'),
(10015,10014,7,'sell',NULL,'全新未穿帆布鞋37码，买小了转','品牌店活动买的，37码偏窄，我穿着顶脚。只在宿舍试穿一次，吊牌还在。\n\n价格：89元\n颜色：米白\n交易：苏州园区可面交，也可邮寄。',0,'approved','normal',8,1,0,4,230,'2026-04-14 13:16:00','2026-04-14 11:30:00',NULL,'2026-04-14 11:25:00','2026-04-14 13:16:00'),
(10016,10015,1,'sell',NULL,'佳能小痰盂镜头EF 50mm f/1.8 STM','换系统后闲置，镜片无霉无雾，外观正常使用痕迹。\n\n适合刚入门拍人像的同学，成像比套头好很多。\n价格：420元\n南京奥体附近面交，支持带机试拍。',0,'approved','normal',35,3,3,15,980,'2026-04-14 15:22:00','2026-04-14 12:05:00',NULL,'2026-04-14 12:00:00','2026-04-14 15:22:00'),
(10017,10016,2,'review',NULL,'Apple Pencil二代二手购买小记','帮室友收了一支Apple Pencil二代，卖家提供了购买记录。\n\n验的时候重点看：吸附充电是否正常、笔尖磨损、压感、双击切换工具、是否有摔弯。最后520元成交，搭配iPad记课设图很方便。',0,'approved','normal',43,4,5,18,1210,'2026-04-14 16:12:00','2026-04-14 13:00:00',NULL,'2026-04-14 12:55:00','2026-04-14 16:12:00'),
(10018,10017,3,'help',NULL,'二手山地车看车要注意哪些地方？','想买一辆通勤山地车，预算800以内。看了几辆捷安特和美利达，不太会判断变速、刹车和车架状态。有没有线下看车清单？',0,'approved','normal',21,3,1,8,540,'2026-04-14 16:40:00','2026-04-14 13:35:00',NULL,'2026-04-14 13:30:00','2026-04-14 16:40:00'),
(10019,10018,6,'sell',NULL,'小米电饭煲1.6L，适合宿舍两人用','毕业前清理小家电，电饭煲用了半年，内胆无明显划痕，蒸米饭和煮粥都正常。\n\n价格：75元\n广州天河可自提，送量杯和饭勺。',0,'approved','normal',11,2,0,6,310,'2026-04-14 17:02:00','2026-04-14 14:10:00',NULL,'2026-04-14 14:05:00','2026-04-14 17:02:00'),
(10020,10019,2,'review',NULL,'考研专业课资料避坑：影印版和笔记怎么选','最近整理资料时发现很多同学容易买重。\n\n建议先确认年份、学校参考书是否更新，再看资料是否含真题解析。只买电子版要注意是不是拼凑截图，纸质版最好当面翻目录。',0,'approved','top',58,6,7,26,1660,'2026-04-14 18:18:00','2026-04-14 14:45:00',NULL,'2026-04-14 14:40:00','2026-04-14 18:18:00'),
(10021,10020,4,'normal',NULL,'大家面交一般约在哪里？','最近第一次在平台卖东西，想问问大家面交通常约校门口、地铁站还是图书馆？贵一点的数码产品是不是最好找有监控的地方？',0,'approved','normal',17,5,2,9,470,'2026-04-14 18:45:00','2026-04-14 15:15:00',NULL,'2026-04-14 15:10:00','2026-04-14 18:45:00'),
(10022,10001,5,'sell',NULL,'罗技K380键盘，粉色，按键正常','自用蓝牙键盘，三设备切换正常，外壳有轻微使用痕迹。\n\n价格：85元\n上海徐汇或杨浦可面交，适合平板记笔记。',0,'approved','normal',24,2,2,11,690,'2026-04-14 19:05:00','2026-04-14 15:50:00',NULL,'2026-04-14 15:45:00','2026-04-14 19:05:00'),
(10023,10002,3,'help',NULL,'买二手显示器需要现场测试坏点吗？','看中一台27寸2K显示器，卖家说无坏点但没有包装。请问面交时用什么网页或软件测坏点、漏光和接口？带笔记本去够不够？',0,'approved','normal',13,3,1,5,360,'2026-04-14 19:30:00','2026-04-14 16:20:00',NULL,'2026-04-14 16:15:00','2026-04-14 19:30:00'),
(10024,10003,1,'sell',NULL,'闲置Xbox手柄白色版，摇杆无漂移','去年买来接电脑打游戏，现在换了精英手柄。\n\n手柄：Xbox Series 白色\n状态：摇杆无漂移，按键回弹正常\n价格：230元\n可当场连电脑测试。',0,'pending','normal',0,0,0,0,86,NULL,NULL,NULL,'2026-04-14 16:55:00','2026-04-14 16:55:00'),
(10025,10004,4,'normal',NULL,'吐槽一下临时鸽子的买家','今天约好中午面交，对方到点说手机没电联系不上。建议大家交易前互相确认时间地点，临时变更也提前说一声，别让人白跑。',0,'approved','normal',31,4,3,13,810,'2026-04-14 20:12:00','2026-04-14 17:20:00',NULL,'2026-04-14 17:15:00','2026-04-14 20:12:00'),
(10026,10005,2,'review',NULL,'宿舍投影仪二手体验：亮度比参数更重要','收了一台1080P投影仪，晚上关灯看电影效果还行，但白天基本不适合。\n\n建议买之前问清楚灯泡时长、遥控器是否原装、有没有明显灰尘斑。不要只看标称流明，最好看实拍视频。',0,'approved','featured',76,5,11,34,2120,'2026-04-14 21:05:00','2026-04-14 18:00:00',NULL,'2026-04-14 17:55:00','2026-04-14 21:05:00'),
(10027,10008,1,'sell',NULL,'MacBook Air M1 8+256，电池循环196','女生自用，外观保护得比较好，A面一处细微划痕。\n\n配置：M1 8G 256G\n电池循环：196，健康90%\n价格：3650元\n支持现场验机，优先同城。',0,'pending','normal',0,0,0,0,132,NULL,NULL,NULL,'2026-04-14 18:30:00','2026-04-14 18:30:00'),
(10028,10006,1,'sell',NULL,'低价出全新品牌手机，先付款包邮','全新未拆封热门手机，市场价便宜一千多。数量有限，不支持面交，只走微信转账，付款后当天发货。',0,'rejected','normal',0,0,0,0,22,NULL,NULL,'疑似引导站外交易且无法核验货源','2026-04-14 19:10:00','2026-04-14 19:25:00');
/*!40000 ALTER TABLE `forum_post` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_post_media` WRITE;
/*!40000 ALTER TABLE `forum_post_media` DISABLE KEYS */;
INSERT INTO `forum_post_media` VALUES
(10,10011,'image','/uploads/posts/10011_1.jpg',NULL,1,'2026-04-14 09:06:00'),(11,10011,'image','/uploads/posts/10011_2.jpg',NULL,2,'2026-04-14 09:06:00'),
(12,10012,'image','/uploads/posts/10012_1.jpg',NULL,1,'2026-04-14 09:42:00'),
(13,10013,'image','/uploads/posts/10013_1.jpg',NULL,1,'2026-04-14 10:12:00'),
(14,10015,'image','/uploads/posts/10015_1.jpg',NULL,1,'2026-04-14 11:26:00'),
(15,10016,'image','/uploads/posts/10016_1.jpg',NULL,1,'2026-04-14 12:01:00'),(16,10016,'image','/uploads/posts/10016_2.jpg',NULL,2,'2026-04-14 12:01:00'),
(17,10019,'image','/uploads/posts/10019_1.jpg',NULL,1,'2026-04-14 14:06:00'),
(18,10022,'image','/uploads/posts/10022_1.jpg',NULL,1,'2026-04-14 15:46:00'),
(19,10024,'image','/uploads/posts/10024_1.jpg',NULL,1,'2026-04-14 16:56:00'),
(20,10026,'image','/uploads/posts/10026_1.jpg',NULL,1,'2026-04-14 17:56:00'),(21,10026,'image','/uploads/posts/10026_2.jpg',NULL,2,'2026-04-14 17:56:00'),
(22,10027,'image','/uploads/posts/10027_1.jpg',NULL,1,'2026-04-14 18:31:00'),(23,10027,'image','/uploads/posts/10027_2.jpg',NULL,2,'2026-04-14 18:31:00');
/*!40000 ALTER TABLE `forum_post_media` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_post_tag` WRITE;
/*!40000 ALTER TABLE `forum_post_tag` DISABLE KEYS */;
INSERT INTO `forum_post_tag` VALUES
(23,10011,1,'2026-04-14 09:05:00'),(24,10011,7,'2026-04-14 09:05:00'),(25,10011,8,'2026-04-14 09:05:00'),
(26,10012,4,'2026-04-14 09:40:00'),(27,10013,5,'2026-04-14 10:10:00'),(28,10013,8,'2026-04-14 10:10:00'),
(29,10014,3,'2026-04-14 10:50:00'),(30,10014,6,'2026-04-14 10:50:00'),
(31,10015,7,'2026-04-14 11:25:00'),(32,10016,1,'2026-04-14 12:00:00'),(33,10016,8,'2026-04-14 12:00:00'),
(34,10017,5,'2026-04-14 12:55:00'),(35,10017,8,'2026-04-14 12:55:00'),
(36,10018,3,'2026-04-14 13:30:00'),(37,10018,6,'2026-04-14 13:30:00'),
(38,10019,4,'2026-04-14 14:05:00'),(39,10020,2,'2026-04-14 14:40:00'),(40,10020,5,'2026-04-14 14:40:00'),
(41,10021,3,'2026-04-14 15:10:00'),(42,10021,4,'2026-04-14 15:10:00'),
(43,10022,1,'2026-04-14 15:45:00'),(44,10022,7,'2026-04-14 15:45:00'),
(45,10023,3,'2026-04-14 16:15:00'),(46,10023,8,'2026-04-14 16:15:00'),
(47,10024,1,'2026-04-14 16:55:00'),(48,10024,7,'2026-04-14 16:55:00'),
(49,10025,6,'2026-04-14 17:15:00'),(50,10026,2,'2026-04-14 17:55:00'),(51,10026,5,'2026-04-14 17:55:00'),
(52,10027,1,'2026-04-14 18:30:00'),(53,10027,8,'2026-04-14 18:30:00'),(54,10028,6,'2026-04-14 19:10:00');
/*!40000 ALTER TABLE `forum_post_tag` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_comment` WRITE;
/*!40000 ALTER TABLE `forum_comment` DISABLE KEYS */;
INSERT INTO `forum_comment` VALUES
(10016,10011,0,NULL,10012,'这个价格挺合适，电池状态怎么样？',0,'approved',4,1,'2026-04-14 09:58:00','2026-04-14 09:58:00'),
(10017,10011,10016,10012,10010,'电池循环不到80次，现场可以看设置。',0,'approved',2,0,'2026-04-14 10:10:00','2026-04-14 10:10:00'),
(10018,10011,0,NULL,10016,'请问带不带笔？',0,'approved',1,0,'2026-04-14 10:42:00','2026-04-14 10:42:00'),
(10019,10012,0,NULL,10015,'桌子可以单买吗？今晚能自提。',0,'approved',3,0,'2026-04-14 10:26:00','2026-04-14 10:26:00'),
(10020,10012,0,NULL,10011,'可以单买，桌子45元。',0,'approved',2,0,'2026-04-14 11:08:00','2026-04-14 11:08:00'),
(10021,10013,0,NULL,10010,'耳罩老化真的要重点看，我之前踩过坑。',0,'approved',7,1,'2026-04-14 11:22:00','2026-04-14 11:22:00'),
(10022,10013,0,NULL,10017,'降噪偏音怎么测比较明显？',0,'approved',3,0,'2026-04-14 12:48:00','2026-04-14 12:48:00'),
(10023,10013,10022,10017,10012,'找风噪比较大的路边，左右耳切换听人声位置。',0,'approved',5,0,'2026-04-14 14:20:00','2026-04-14 14:20:00'),
(10024,10014,0,NULL,10013,'安全座椅如果出过事故就不要买，外观很难判断。',0,'approved',9,2,'2026-04-14 11:40:00','2026-04-14 11:40:00'),
(10025,10014,10024,10013,10018,'同意，最好买能提供购买记录且年限短的。',0,'approved',4,0,'2026-04-14 12:03:00','2026-04-14 12:03:00'),
(10026,10014,0,NULL,10020,'看标签上的生产日期，超过使用年限别要。',0,'approved',6,0,'2026-04-14 12:30:00','2026-04-14 12:30:00'),
(10027,10015,0,NULL,10020,'鞋还在吗？可以邮寄重庆吗？',0,'approved',1,0,'2026-04-14 13:16:00','2026-04-14 13:16:00'),
(10028,10016,0,NULL,10007,'这个镜头拍人像很够用，价格也合理。',0,'approved',6,1,'2026-04-14 12:50:00','2026-04-14 12:50:00'),
(10029,10016,0,NULL,10017,'能不能周末南京南站附近面交？',0,'approved',2,0,'2026-04-14 15:22:00','2026-04-14 15:22:00'),
(10030,10017,0,NULL,10001,'压感测试可以用备忘录画线，挺直观。',0,'approved',5,0,'2026-04-14 13:35:00','2026-04-14 13:35:00'),
(10031,10017,0,NULL,10016,'笔尖如果已经磨平，记得把换笔尖成本算进去。',0,'approved',3,0,'2026-04-14 15:10:00','2026-04-14 15:10:00'),
(10032,10017,0,NULL,10002,'刚好想买，谢谢清单。',0,'approved',2,0,'2026-04-14 16:12:00','2026-04-14 16:12:00'),
(10033,10018,0,NULL,10015,'看车架焊点和前叉漏油，变速每一档都试一下。',0,'approved',4,0,'2026-04-14 14:06:00','2026-04-14 14:06:00'),
(10034,10018,0,NULL,10003,'刹车片和轮胎也要看，耗材换一套不便宜。',0,'approved',3,0,'2026-04-14 15:38:00','2026-04-14 15:38:00'),
(10035,10018,0,NULL,10018,'收到，我周末带朋友一起去看。',0,'approved',1,0,'2026-04-14 16:40:00','2026-04-14 16:40:00'),
(10036,10019,0,NULL,10011,'这个容量两个人够用吗？',0,'approved',2,0,'2026-04-14 15:12:00','2026-04-14 15:12:00'),
(10037,10019,0,NULL,10018,'够两个人，三个人饭量大就偏小。',0,'approved',1,0,'2026-04-14 17:02:00','2026-04-14 17:02:00'),
(10038,10020,0,NULL,10002,'真题解析比单纯答案重要太多了。',0,'approved',8,1,'2026-04-14 15:30:00','2026-04-14 15:30:00'),
(10039,10020,0,NULL,10019,'纸质版建议当面翻年份，我去年买到旧版。',0,'approved',5,0,'2026-04-14 16:45:00','2026-04-14 16:45:00'),
(10040,10020,0,NULL,10005,'电子资料最好先要目录截图。',0,'approved',4,0,'2026-04-14 17:30:00','2026-04-14 17:30:00'),
(10041,10020,0,NULL,10016,'已经收藏，暑假开始准备。',0,'approved',2,0,'2026-04-14 18:18:00','2026-04-14 18:18:00'),
(10042,10021,0,NULL,10001,'我一般约校门口保安亭旁边，有监控也好找。',0,'approved',5,0,'2026-04-14 15:52:00','2026-04-14 15:52:00'),
(10043,10021,0,NULL,10010,'贵重数码建议白天面交，别去太偏的地方。',0,'approved',6,0,'2026-04-14 17:12:00','2026-04-14 17:12:00'),
(10044,10021,0,NULL,10014,'女生单独面交可以带同学一起。',0,'approved',7,0,'2026-04-14 18:20:00','2026-04-14 18:20:00'),
(10045,10021,0,NULL,10020,'谢谢大家，学到了。',0,'approved',1,0,'2026-04-14 18:45:00','2026-04-14 18:45:00'),
(10046,10022,0,NULL,10008,'键盘还在吗？可以明天杨浦面交。',0,'approved',2,0,'2026-04-14 18:08:00','2026-04-14 18:08:00'),
(10047,10022,0,NULL,10001,'还在，明天下午可以。',0,'approved',1,0,'2026-04-14 19:05:00','2026-04-14 19:05:00'),
(10048,10023,0,NULL,10007,'带笔记本够了，浏览器搜显示器坏点测试就能全屏看。',0,'approved',4,0,'2026-04-14 17:00:00','2026-04-14 17:00:00'),
(10049,10023,0,NULL,10012,'HDMI和DP都试一下，别只看屏幕。',0,'approved',3,0,'2026-04-14 18:36:00','2026-04-14 18:36:00'),
(10050,10023,0,NULL,10002,'明白了，我带两根线过去。',0,'approved',1,0,'2026-04-14 19:30:00','2026-04-14 19:30:00'),
(10051,10025,0,NULL,10011,'建议约前半小时再确认一次，能少很多麻烦。',0,'approved',6,1,'2026-04-14 18:10:00','2026-04-14 18:10:00'),
(10052,10025,0,NULL,10015,'我会在商品描述里写清楚迟到多久默认取消。',0,'approved',5,0,'2026-04-14 19:05:00','2026-04-14 19:05:00'),
(10053,10025,0,NULL,10004,'下次我也提前确认，今天确实浪费时间。',0,'approved',2,0,'2026-04-14 20:12:00','2026-04-14 20:12:00'),
(10054,10026,0,NULL,10018,'投影仪灰尘斑真的影响观感，白墙一照很明显。',0,'approved',7,1,'2026-04-14 18:45:00','2026-04-14 18:45:00'),
(10055,10026,0,NULL,10012,'白天用是不是必须上高流明？',0,'approved',3,0,'2026-04-14 19:26:00','2026-04-14 19:26:00'),
(10056,10026,10055,10012,10005,'宿舍白天窗帘遮光一般，建议别指望投影替代显示器。',0,'approved',4,0,'2026-04-14 20:30:00','2026-04-14 20:30:00'),
(10057,10026,0,NULL,10020,'有实拍视频会靠谱很多。',0,'approved',2,0,'2026-04-14 21:05:00','2026-04-14 21:05:00');
/*!40000 ALTER TABLE `forum_comment` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_collect` WRITE;
/*!40000 ALTER TABLE `forum_collect` DISABLE KEYS */;
INSERT INTO `forum_collect` VALUES
(8,10012,10011,'2026-04-14 09:50:00'),(9,10016,10011,'2026-04-14 10:00:00'),
(10,10015,10012,'2026-04-14 10:25:00'),(11,10010,10013,'2026-04-14 11:18:00'),(12,10017,10013,'2026-04-14 12:49:00'),
(13,10013,10014,'2026-04-14 11:42:00'),(14,10020,10014,'2026-04-14 12:33:00'),(15,10020,10015,'2026-04-14 13:17:00'),
(16,10007,10016,'2026-04-14 12:52:00'),(17,10017,10016,'2026-04-14 15:25:00'),(18,10001,10017,'2026-04-14 13:36:00'),
(19,10016,10017,'2026-04-14 15:11:00'),(20,10015,10018,'2026-04-14 14:08:00'),(21,10011,10019,'2026-04-14 15:13:00'),
(22,10002,10020,'2026-04-14 15:32:00'),(23,10019,10020,'2026-04-14 16:46:00'),(24,10005,10020,'2026-04-14 17:31:00'),
(25,10001,10021,'2026-04-14 15:53:00'),(26,10014,10021,'2026-04-14 18:21:00'),(27,10008,10022,'2026-04-14 18:09:00'),
(28,10007,10023,'2026-04-14 17:01:00'),(29,10012,10023,'2026-04-14 18:37:00'),(30,10011,10025,'2026-04-14 18:11:00'),
(31,10018,10026,'2026-04-14 18:46:00'),(32,10012,10026,'2026-04-14 19:27:00'),(33,10020,10026,'2026-04-14 21:06:00');
/*!40000 ALTER TABLE `forum_collect` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_reaction` WRITE;
/*!40000 ALTER TABLE `forum_reaction` DISABLE KEYS */;
INSERT INTO `forum_reaction` VALUES
(15,'post',10011,10012,'like','2026-04-14 09:50:00'),(16,'post',10011,10016,'like','2026-04-14 10:00:00'),
(17,'post',10012,10015,'like','2026-04-14 10:25:00'),(18,'post',10013,10010,'like','2026-04-14 11:18:00'),(19,'post',10013,10017,'like','2026-04-14 12:49:00'),
(20,'post',10014,10013,'like','2026-04-14 11:42:00'),(21,'post',10014,10020,'like','2026-04-14 12:33:00'),(22,'post',10015,10020,'like','2026-04-14 13:17:00'),
(23,'post',10016,10007,'like','2026-04-14 12:52:00'),(24,'post',10016,10017,'like','2026-04-14 15:25:00'),(25,'post',10017,10001,'like','2026-04-14 13:36:00'),
(26,'post',10017,10016,'like','2026-04-14 15:11:00'),(27,'post',10018,10015,'like','2026-04-14 14:08:00'),(28,'post',10019,10011,'like','2026-04-14 15:13:00'),
(29,'post',10020,10002,'like','2026-04-14 15:32:00'),(30,'post',10020,10019,'like','2026-04-14 16:46:00'),(31,'post',10020,10005,'like','2026-04-14 17:31:00'),
(32,'post',10021,10001,'like','2026-04-14 15:53:00'),(33,'post',10021,10014,'like','2026-04-14 18:21:00'),(34,'post',10022,10008,'like','2026-04-14 18:09:00'),
(35,'post',10023,10007,'like','2026-04-14 17:01:00'),(36,'post',10023,10012,'like','2026-04-14 18:37:00'),(37,'post',10025,10011,'like','2026-04-14 18:11:00'),
(38,'post',10026,10018,'like','2026-04-14 18:46:00'),(39,'post',10026,10012,'like','2026-04-14 19:27:00'),(40,'post',10026,10020,'like','2026-04-14 21:06:00'),
(41,'comment',10021,10010,'like','2026-04-14 11:30:00'),(42,'comment',10024,10018,'like','2026-04-14 12:05:00'),(43,'comment',10028,10015,'like','2026-04-14 12:55:00'),
(44,'comment',10038,10016,'like','2026-04-14 18:20:00'),(45,'comment',10051,10004,'like','2026-04-14 20:00:00'),(46,'comment',10054,10005,'like','2026-04-14 20:10:00');
/*!40000 ALTER TABLE `forum_reaction` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_post_share` WRITE;
/*!40000 ALTER TABLE `forum_post_share` DISABLE KEYS */;
INSERT INTO `forum_post_share` VALUES
(6,10013,10010,'in_app','2026-04-14 11:20:00'),(7,10013,10017,'copy_link','2026-04-14 12:50:00'),
(8,10016,10007,'wechat','2026-04-14 12:54:00'),(9,10017,10001,'in_app','2026-04-14 13:38:00'),
(10,10020,10002,'copy_link','2026-04-14 15:34:00'),(11,10020,10019,'wechat','2026-04-14 16:48:00'),
(12,10021,10014,'in_app','2026-04-14 18:22:00'),(13,10025,10011,'copy_link','2026-04-14 18:12:00'),
(14,10026,10018,'wechat','2026-04-14 18:48:00'),(15,10026,10012,'qq','2026-04-14 19:28:00');
/*!40000 ALTER TABLE `forum_post_share` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_post_view_daily` WRITE;
/*!40000 ALTER TABLE `forum_post_view_daily` DISABLE KEYS */;
INSERT INTO `forum_post_view_daily` VALUES
(5,10011,'2026-04-14',68,180,'2026-04-14 23:00:00'),(6,10012,'2026-04-14',39,92,'2026-04-14 23:00:00'),
(7,10013,'2026-04-14',126,340,'2026-04-14 23:00:00'),(8,10014,'2026-04-14',58,140,'2026-04-14 23:00:00'),
(9,10015,'2026-04-14',22,51,'2026-04-14 23:00:00'),(10,10016,'2026-04-14',86,220,'2026-04-14 23:00:00'),
(11,10017,'2026-04-14',97,260,'2026-04-14 23:00:00'),(12,10018,'2026-04-14',50,120,'2026-04-14 23:00:00'),
(13,10019,'2026-04-14',31,74,'2026-04-14 23:00:00'),(14,10020,'2026-04-14',145,388,'2026-04-14 23:00:00'),
(15,10021,'2026-04-14',44,105,'2026-04-14 23:00:00'),(16,10022,'2026-04-14',63,151,'2026-04-14 23:00:00'),
(17,10023,'2026-04-14',35,84,'2026-04-14 23:00:00'),(18,10024,'2026-04-14',18,38,'2026-04-14 23:00:00'),
(19,10025,'2026-04-14',72,190,'2026-04-14 23:00:00'),(20,10026,'2026-04-14',164,430,'2026-04-14 23:00:00'),
(21,10027,'2026-04-14',42,95,'2026-04-14 23:00:00'),(22,10028,'2026-04-14',9,22,'2026-04-14 23:00:00');
/*!40000 ALTER TABLE `forum_post_view_daily` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_audit_log` WRITE;
/*!40000 ALTER TABLE `forum_audit_log` DISABLE KEYS */;
INSERT INTO `forum_audit_log` VALUES
(2,'post',10028,900003,'reject','疑似引导站外交易且无法核验货源','pending','rejected','2026-04-14 19:25:00');
/*!40000 ALTER TABLE `forum_audit_log` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `forum_report` WRITE;
/*!40000 ALTER TABLE `forum_report` DISABLE KEYS */;
INSERT INTO `forum_report` VALUES
(3,'post',10028,10010,'欺诈','要求站外转账且不支持面交，疑似诈骗',NULL,'resolved',900003,'已驳回该帖子，提醒用户不要站外交易','2026-04-14 19:18:00','2026-04-14 19:26:00'),
(4,'comment',10051,10004,'其他','评论建议合理，但语气略冲，申请平台查看',NULL,'pending',NULL,NULL,NULL,'2026-04-14 20:15:00');
/*!40000 ALTER TABLE `forum_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logistics_trace`
--

DROP TABLE IF EXISTS `logistics_trace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logistics_trace` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '物流轨迹ID',
  `shipment_id` bigint unsigned NOT NULL COMMENT '发货记录ID',
  `trace_time` datetime NOT NULL COMMENT '轨迹时间',
  `trace_status` varchar(100) NOT NULL COMMENT '轨迹状态',
  `trace_detail` varchar(500) NOT NULL COMMENT '轨迹详情',
  `trace_location` varchar(255) DEFAULT NULL COMMENT '轨迹地点',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_trace_shipment_time` (`shipment_id`,`trace_time`),
  CONSTRAINT `fk_logistics_trace_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `order_shipment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物流轨迹明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistics_trace`
--

LOCK TABLES `logistics_trace` WRITE;
/*!40000 ALTER TABLE `logistics_trace` DISABLE KEYS */;
INSERT INTO `logistics_trace` VALUES (1,1,'2026-04-13 12:27:05','已发货','卖家已发货，包裹已交由物流承运',NULL,'2026-04-13 12:27:04'),(2,1,'2026-04-13 14:20:00','运输中','包裹已到达上海转运中心','上海市','2026-04-13 12:31:14'),(3,1,'2026-04-13 12:33:22','已签收','买家已完成签收',NULL,'2026-04-13 12:33:22'),(4,2,'2026-04-13 12:40:53','待自提','卖家已备货完成，等待买家自提','深圳大学沧海致腾楼240','2026-04-13 12:40:52'),(5,2,'2026-04-13 13:01:30','已自提','卖家已完成自提核销','深圳大学沧海致腾楼240','2026-04-13 13:01:30');
/*!40000 ALTER TABLE `logistics_trace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `product_title` varchar(150) NOT NULL COMMENT '下单时商品标题快照',
  `product_image_url` varchar(500) DEFAULT NULL COMMENT '下单时封面图快照',
  `unit_price` decimal(10,2) NOT NULL COMMENT '成交单价（元）',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `subtotal_amount` decimal(10,2) NOT NULL COMMENT '小计金额（元）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_product` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `chk_order_item_amount` CHECK (((`unit_price` >= 0) and (`subtotal_amount` >= 0))),
  CONSTRAINT `chk_order_item_quantity` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=950010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表（支持多商品扩展）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (950001,940001,920001,'iPhone 13 128G 星光色','product/900001/2c6a7d29-491e-455f-bd0f-126cc7ba6f37.webp',2999.00,1,2999.00,'2026-04-12 10:00:00'),(950002,940002,920002,'ThinkPad X1 Carbon 2022','product/900001/1c4b716f-d594-4c08-90d6-4cb7a9c3245c.webp',3499.00,1,3499.00,'2026-04-12 10:05:00'),(950003,940003,920001,'iPhone 13 128G 星光色','product/900001/2c6a7d29-491e-455f-bd0f-126cc7ba6f37.webp',2999.00,1,2999.00,'2026-04-12 11:30:00'),(950004,940004,920001,'iPhone 13 128G 星光色','product/900001/2c6a7d29-491e-455f-bd0f-126cc7ba6f37.webp',2999.00,1,2999.00,'2026-04-12 22:27:49'),(950005,940005,920003,'AirPods Pro 二代','product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',899.00,2,1798.00,'2026-04-12 22:37:57'),(950006,940006,920003,'AirPods Pro 二代','product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',899.00,2,1798.00,'2026-04-13 00:25:42'),(950007,940007,920003,'AirPods Pro 二代','product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',899.00,2,1798.00,'2026-04-13 00:25:52'),(950008,940008,920003,'AirPods Pro 二代','product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',899.00,2,1798.00,'2026-04-13 11:54:33'),(950009,940009,920003,'AirPods Pro 二代','product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',899.00,2,1798.00,'2026-04-13 12:36:57');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_shipment`
--

DROP TABLE IF EXISTS `order_shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_shipment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '发货记录ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `shipment_type` enum('shipping','pickup') NOT NULL COMMENT '交付类型',
  `logistics_company` varchar(100) DEFAULT NULL COMMENT '物流公司',
  `tracking_no` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `shipment_status` enum('to_ship','in_transit','signed','lost','exception') NOT NULL DEFAULT 'to_ship' COMMENT '物流状态',
  `shipped_by` bigint unsigned DEFAULT NULL COMMENT '操作发货用户ID',
  `shipped_at` datetime DEFAULT NULL COMMENT '发货时间',
  `signed_at` datetime DEFAULT NULL COMMENT '签收时间',
  `pickup_code` varchar(20) DEFAULT NULL COMMENT '自提码（自提场景）',
  `pickup_verified_at` datetime DEFAULT NULL COMMENT '自提核销时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_shipment_order` (`order_id`),
  KEY `idx_order_shipment_tracking` (`tracking_no`),
  KEY `fk_order_shipment_user` (`shipped_by`),
  CONSTRAINT `fk_order_shipment_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_order_shipment_user` FOREIGN KEY (`shipped_by`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单发货与交付表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_shipment`
--

LOCK TABLES `order_shipment` WRITE;
/*!40000 ALTER TABLE `order_shipment` DISABLE KEYS */;
INSERT INTO `order_shipment` VALUES (1,940006,'shipping','顺丰速运','SF1234567890','signed',900002,'2026-04-13 12:27:05','2026-04-13 12:33:22',NULL,NULL,'2026-04-13 12:27:04','2026-04-13 12:27:04'),(2,940009,'pickup',NULL,NULL,'signed',900002,'2026-04-13 12:40:53','2026-04-13 13:01:30','750355','2026-04-13 13:01:30','2026-04-13 12:40:52','2026-04-13 12:40:52');
/*!40000 ALTER TABLE `order_shipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_log`
--

DROP TABLE IF EXISTS `order_status_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单状态日志ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `from_status` varchar(50) DEFAULT NULL COMMENT '变更前状态',
  `to_status` varchar(50) NOT NULL COMMENT '变更后状态',
  `changed_by` bigint unsigned DEFAULT NULL COMMENT '操作人用户ID',
  `change_reason` varchar(255) DEFAULT NULL COMMENT '变更原因',
  `changed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_status_log_order` (`order_id`,`changed_at`),
  KEY `fk_order_status_log_user` (`changed_by`),
  CONSTRAINT `fk_order_status_log_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_order_status_log_user` FOREIGN KEY (`changed_by`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=960023 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单状态流转日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_log`
--

LOCK TABLES `order_status_log` WRITE;
/*!40000 ALTER TABLE `order_status_log` DISABLE KEYS */;
INSERT INTO `order_status_log` VALUES (960001,940001,NULL,'pending_payment',10001,'创建订单','2026-04-12 10:00:00'),(960002,940002,NULL,'pending_payment',10001,'创建订单','2026-04-12 10:05:00'),(960003,940002,'pending_payment','paid_pending_ship',10001,'支付成功','2026-04-12 10:10:00'),(960004,940002,'paid_pending_ship','shipped',900001,'卖家发货','2026-04-12 11:00:00'),(960005,940003,NULL,'pending_payment',10001,'创建订单','2026-04-12 11:30:00'),(960006,940003,'pending_payment','cancelled',10001,'买家临时取消','2026-04-12 12:00:00'),(960007,940004,NULL,'pending_payment',10009,'创建订单','2026-04-12 22:27:49'),(960008,940005,NULL,'pending_payment',10009,'创建订单','2026-04-12 22:37:57'),(960009,940005,'pending_payment','cancelled',10009,'买家临时不需要了','2026-04-12 22:39:23'),(960010,940006,NULL,'pending_payment',10009,'创建订单','2026-04-13 00:25:42'),(960011,940007,NULL,'pending_payment',10009,'创建订单','2026-04-13 00:25:52'),(960012,940007,'pending_payment','cancelled',10009,'买家临时不需要了','2026-04-13 00:26:38'),(960013,940008,NULL,'pending_payment',10009,'创建订单','2026-04-13 11:54:33'),(960014,940008,'pending_payment','cancelled',10009,'买家临时不需要了','2026-04-13 11:56:03'),(960015,940006,'pending_payment','paid_pending_ship',NULL,'支付回调成功','2026-04-13 12:05:36'),(960016,940006,'paid_pending_ship','shipped',900002,'卖家已发货','2026-04-13 12:27:04'),(960017,940006,'shipped','delivered',10009,'买家已签收','2026-04-13 12:33:22'),(960018,940009,NULL,'pending_payment',10009,'创建订单','2026-04-13 12:36:57'),(960019,940009,'pending_payment','paid_pending_ship',NULL,'支付回调成功','2026-04-13 12:38:36'),(960020,940009,'paid_pending_ship','shipped',900002,'卖家已创建自提记录','2026-04-13 12:40:52'),(960021,940009,'shipped','delivered',900002,'卖家完成自提核销','2026-04-13 13:01:30'),(960022,940006,'delivered','completed',10009,'买家确认收货','2026-04-13 13:03:18');
/*!40000 ALTER TABLE `order_status_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_order`
--

DROP TABLE IF EXISTS `payment_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '支付单ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `payment_no` varchar(64) NOT NULL COMMENT '支付单号（业务唯一）',
  `payment_channel` enum('wechat','alipay','balance') NOT NULL COMMENT '支付渠道',
  `payment_status` enum('created','paying','paid','failed','closed','refunded') NOT NULL DEFAULT 'created' COMMENT '支付状态',
  `payable_amount` decimal(10,2) NOT NULL COMMENT '应付金额（元）',
  `paid_amount` decimal(10,2) DEFAULT NULL COMMENT '实付金额（元）',
  `channel_trade_no` varchar(100) DEFAULT NULL COMMENT '三方支付流水号',
  `paid_at` datetime DEFAULT NULL COMMENT '支付完成时间',
  `failed_reason` varchar(255) DEFAULT NULL COMMENT '支付失败原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_order_no` (`payment_no`),
  UNIQUE KEY `uk_payment_order_order` (`order_id`),
  KEY `idx_payment_order_status` (`payment_status`),
  CONSTRAINT `fk_payment_order_trade_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `chk_payment_order_amount` CHECK ((`payable_amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_order`
--

LOCK TABLES `payment_order` WRITE;
/*!40000 ALTER TABLE `payment_order` DISABLE KEYS */;
INSERT INTO `payment_order` VALUES (1,940006,'PO202604131157018840','alipay','paid',1813.00,1813.00,'PO202604131157018840','2026-04-13 12:05:37',NULL,'2026-04-13 11:57:01','2026-04-13 11:57:01'),(2,940009,'PO202604131237491420','alipay','paid',1813.00,1813.00,'PO202604131157018840','2026-04-13 12:38:37',NULL,'2026-04-13 12:37:49','2026-04-13 12:37:49');
/*!40000 ALTER TABLE `payment_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_transaction`
--

DROP TABLE IF EXISTS `payment_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_transaction` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '支付交易流水ID',
  `payment_order_id` bigint unsigned NOT NULL COMMENT '支付单ID',
  `transaction_type` enum('pay','refund','adjust') NOT NULL COMMENT '交易类型',
  `transaction_status` enum('processing','success','failed') NOT NULL COMMENT '交易状态',
  `amount` decimal(10,2) NOT NULL COMMENT '交易金额（元）',
  `channel_trade_no` varchar(100) DEFAULT NULL COMMENT '渠道流水号',
  `channel_response` json DEFAULT NULL COMMENT '渠道响应报文',
  `occurred_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_payment_transaction_order` (`payment_order_id`,`occurred_at`),
  CONSTRAINT `fk_payment_transaction_payment_order` FOREIGN KEY (`payment_order_id`) REFERENCES `payment_order` (`id`),
  CONSTRAINT `chk_payment_transaction_amount` CHECK ((`amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付交易流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_transaction`
--

LOCK TABLES `payment_transaction` WRITE;
/*!40000 ALTER TABLE `payment_transaction` DISABLE KEYS */;
INSERT INTO `payment_transaction` VALUES (1,1,'pay','processing',1813.00,'ALI_2026041312005276066','{\"message\": \"waiting callback\"}','2026-04-13 12:00:52'),(2,1,'pay','success',1813.00,'PO202604131157018840','{\"message\": \"callback paid\"}','2026-04-13 12:05:36'),(3,2,'pay','processing',1813.00,'ALI_2026041312380610329','{\"message\": \"waiting callback\"}','2026-04-13 12:38:06'),(4,2,'pay','success',1813.00,'PO202604131157018840','{\"message\": \"callback paid\"}','2026-04-13 12:38:36');
/*!40000 ALTER TABLE `payment_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家用户ID（来自user_account，可与买家身份共存）',
  `category_id` bigint unsigned NOT NULL COMMENT '商品分类ID',
  `title` varchar(150) NOT NULL COMMENT '商品标题',
  `subtitle` varchar(255) DEFAULT NULL COMMENT '商品副标题',
  `description` text NOT NULL COMMENT '商品详细描述',
  `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `condition_level` enum('new','almost_new','good','fair','poor') NOT NULL COMMENT '新旧程度',
  `purchase_year` smallint DEFAULT NULL COMMENT '购买年份',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价（元）',
  `selling_price` decimal(10,2) NOT NULL COMMENT '出售价格（元）',
  `can_bargain` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可议价：0否1是',
  `trade_mode` enum('pickup','shipping','both') NOT NULL DEFAULT 'both' COMMENT '交易方式：自提/邮寄/都支持',
  `pickup_city` varchar(100) DEFAULT NULL COMMENT '自提城市',
  `pickup_address` varchar(255) DEFAULT NULL COMMENT '自提地点描述',
  `location_lat` decimal(10,7) DEFAULT NULL COMMENT '卖家纬度（用于距离筛选）',
  `location_lng` decimal(10,7) DEFAULT NULL COMMENT '卖家经度（用于距离筛选）',
  `stock` int NOT NULL DEFAULT '1' COMMENT '库存数量（二手通常为1）',
  `publish_status` enum('draft','pending_review','on_sale','reserved','sold','off_shelf','rejected','deleted') NOT NULL DEFAULT 'pending_review' COMMENT '发布状态',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `published_at` datetime DEFAULT NULL COMMENT '上架时间',
  `off_shelf_at` datetime DEFAULT NULL COMMENT '下架时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '审核驳回原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_seller` (`seller_id`),
  KEY `idx_product_category` (`category_id`),
  KEY `idx_product_price` (`selling_price`),
  KEY `idx_product_status` (`publish_status`),
  KEY `idx_product_publish_time` (`published_at`),
  FULLTEXT KEY `ft_product_search` (`title`,`subtitle`,`description`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_product_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `chk_product_price` CHECK ((`selling_price` >= 0)),
  CONSTRAINT `chk_product_stock` CHECK ((`stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=920004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (920001,900001,910001,'iPhone 13 128G 星光色','电池健康 89%，箱说齐全','自用机，无拆修，屏幕和摄像头功能正常，边框有轻微使用痕迹。','Apple','iPhone 13','almost_new',2023,5199.00,2999.00,1,'both','上海','徐家汇地铁站2号口',31.1936000,121.4368000,19,'on_sale',263,21,'2026-04-12 09:00:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-12 22:27:49'),(920002,900001,910002,'ThinkPad X1 Carbon 2022','i5/16G/512G，适合办公','公司备用机闲置，键盘和屏幕状态良好，接口齐全，可提供原装电源。','Lenovo','X1 Carbon','good',2022,7999.00,3499.00,1,'shipping','上海','漕河泾开发区',31.1707000,121.3978000,10,'on_sale',188,16,'2026-04-12 09:10:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(920003,900002,910001,'AirPods Pro 二代','降噪正常，附保护套','通勤使用一年，耳机和充电盒均可正常充电，已清洁消毒。','Apple','AirPods Pro 2','good',2023,1999.00,899.00,1,'both','北京','望京SOHO',39.9968000,116.4815000,11,'on_sale',146,13,'2026-04-12 09:20:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-13 12:36:57'),(920004,900001,910003,'高等数学教材套装','同济七版上下册，附习题册','书页完整，有少量笔记，不影响阅读，适合大一课程复习。','同济大学数学系','高等数学第七版','good',2021,96.00,38.00,1,'pickup','上海','上海大学宝山校区东门',31.3203000,121.3937000,3,'on_sale',128,5,'2026-04-14 09:10:00',NULL,NULL,'2026-04-14 09:05:00','2026-04-14 09:10:00'),(920005,900001,910004,'捷安特山地车 ATX 660','适合校园通勤，可面交试骑','车架无变形，刹车和变速已保养，坐垫略有磨损。','GIANT','ATX 660','fair',2020,1899.00,680.00,1,'pickup','上海','五角场万达广场南门',31.3022000,121.5148000,1,'on_sale',96,7,'2026-04-14 09:20:00',NULL,NULL,'2026-04-14 09:15:00','2026-04-14 09:20:00'),(920006,900002,910005,'宜家 LERSTA 落地灯','暖光灯泡，适合租房使用','灯罩有轻微使用痕迹，功能正常，可拆装打包。','IKEA','LERSTA','good',2022,129.00,55.00,0,'both','北京','海淀黄庄地铁站A口',39.9756000,116.3176000,2,'on_sale',54,4,'2026-04-14 09:30:00',NULL,NULL,'2026-04-14 09:25:00','2026-04-14 09:30:00'),(920007,900002,910006,'Nike Heritage 双肩包','通勤书包，容量约25L','拉链顺滑，肩带完好，外层有一处很浅的摩擦印。','Nike','Heritage','good',2023,299.00,99.00,1,'shipping','北京','望京SOHO',39.9968000,116.4815000,4,'on_sale',73,6,'2026-04-14 09:40:00',NULL,NULL,'2026-04-14 09:35:00','2026-04-14 09:40:00'),(920008,10003,910002,'罗技 MX Master 3 无线鼠标','USB接收器齐全','按键正常，滚轮无异响，适合办公和设计软件使用。','Logitech','MX Master 3','almost_new',2023,699.00,329.00,1,'shipping','杭州','滨江区物联网街',30.1880000,120.2106000,2,'on_sale',142,12,'2026-04-14 10:00:00',NULL,NULL,'2026-04-14 09:55:00','2026-04-14 10:00:00'),(920009,10007,910001,'索尼 A6000 微单套机','含16-50套头，快门约8000','机身轻微磨损，CMOS干净，适合入门摄影。','Sony','A6000','good',2019,3999.00,2180.00,1,'both','广州','体育西路地铁站G口',23.1360000,113.3213000,1,'on_sale',210,18,'2026-04-14 10:10:00',NULL,NULL,'2026-04-14 10:05:00','2026-04-14 10:10:00'),(920010,10005,910003,'英语四级真题资料','近三年真题，含听力二维码','部分页面有荧光笔标记，答案解析完整。','星火英语','CET-4','fair',2024,59.80,18.00,0,'shipping','南京','仙林大学城',32.1154000,118.9304000,5,'on_sale',38,2,'2026-04-14 10:20:00',NULL,NULL,'2026-04-14 10:15:00','2026-04-14 10:20:00'),(920011,10003,910007,'小米电饭煲 3L','内胆完好，适合两到三人','已清洁消毒，功能正常，预约煮饭和保温都可用。','Xiaomi','MFB2AM','good',2021,299.00,120.00,1,'both','杭州','西湖区文三路',30.2741000,120.1365000,1,'reserved',65,3,'2026-04-14 10:30:00',NULL,NULL,'2026-04-14 10:25:00','2026-04-14 10:30:00'),(920012,10010,910002,'MacBook Air M1 8G 256G','深空灰，循环 246 次','键盘、触控板、屏幕显示正常，外壳有两处轻微磕碰，附原装充电器。','Apple','MacBook Air M1','good',2021,7999.00,4380.00,1,'both','深圳','南山科技园深大地铁站',22.5398000,113.9430000,1,'on_sale',256,26,'2026-04-14 10:40:00',NULL,NULL,'2026-04-14 10:35:00','2026-04-14 10:40:00'),(920013,10011,910008,'雅诗兰黛小棕瓶 50ml','未拆封，专柜购入','朋友赠送未使用，包装完整，介意保质期可先私聊确认。','Estee Lauder','Advanced Night Repair','new',2025,850.00,520.00,0,'shipping','成都','春熙路商圈',30.6570000,104.0808000,1,'on_sale',84,9,'2026-04-14 10:50:00',NULL,NULL,'2026-04-14 10:45:00','2026-04-14 10:50:00'),(920014,10012,910009,'Yamaha F310 民谣吉他','原装琴包，适合新手','琴颈稳定，弦距正常，送变调夹和拨片。','Yamaha','F310','good',2020,999.00,480.00,1,'pickup','武汉','街道口地铁站B口',30.5269000,114.3504000,1,'on_sale',117,10,'2026-04-14 11:00:00',NULL,NULL,'2026-04-14 10:55:00','2026-04-14 11:00:00'),(920015,10013,910010,'好孩子婴儿推车','可坐可躺，带遮阳棚','宝宝长大闲置，车轮顺滑，有正常使用痕迹，建议同城自提。','Goodbaby','D819','good',2022,899.00,260.00,1,'pickup','深圳','宝安中心地铁站',22.5537000,113.8831000,1,'on_sale',93,8,'2026-04-14 11:10:00',NULL,NULL,'2026-04-14 11:05:00','2026-04-14 11:10:00'),(920016,10014,910006,'优衣库羊毛大衣 M 码','深灰色，干洗后闲置','版型挺括，无明显起球，适合 165-175cm。','UNIQLO','Wool Coat','almost_new',2023,699.00,220.00,1,'shipping','苏州','工业园区星湖街',31.3174000,120.6813000,1,'on_sale',75,5,'2026-04-14 11:20:00',NULL,NULL,'2026-04-14 11:15:00','2026-04-14 11:20:00'),(920017,10015,910005,'实木床头柜一对','搬家出清，需自提','抽屉顺滑，表面有轻微划痕，两个一起出。','原木良品','北欧床头柜','fair',2021,598.00,160.00,1,'pickup','南京','鼓楼区龙江',32.0621000,118.7368000,1,'on_sale',49,3,'2026-04-14 11:30:00',NULL,NULL,'2026-04-14 11:25:00','2026-04-14 11:30:00'),(920018,10016,910001,'华为 MatePad 11 平板','6G/128G，带手写笔','屏幕贴膜使用，无明显划痕，适合记笔记和看课件。','Huawei','MatePad 11','good',2022,2499.00,1180.00,1,'both','西安','小寨地铁站',34.2293000,108.9451000,1,'on_sale',165,14,'2026-04-14 11:40:00',NULL,NULL,'2026-04-14 11:35:00','2026-04-14 11:40:00'),(920019,10017,910004,'迪卡侬瑜伽垫 8mm','粉色，加厚防滑','买来后使用不多，已清洁，可卷起邮寄。','Decathlon','Yoga Mat 8mm','almost_new',2024,129.00,45.00,0,'shipping','长沙','岳麓大学城',28.1783000,112.9346000,2,'on_sale',42,4,'2026-04-14 11:50:00',NULL,NULL,'2026-04-14 11:45:00','2026-04-14 11:50:00'),(920020,10018,910007,'戴森吹风机 HD08','紫红色，功能正常','风嘴齐全，机身有轻微使用痕迹，支持当面验货。','Dyson','HD08','good',2022,2990.00,1680.00,1,'both','广州','珠江新城地铁站',23.1196000,113.3210000,1,'on_sale',221,20,'2026-04-14 12:00:00',NULL,NULL,'2026-04-14 11:55:00','2026-04-14 12:00:00'),(920021,10019,910003,'考研政治资料全套','肖秀荣、腿姐资料打包','书本保存较好，部分题册有铅笔痕迹，适合二轮复习。','肖秀荣','考研政治','fair',2025,220.00,66.00,1,'shipping','天津','南开大学八里台校区',39.1013000,117.1699000,1,'on_sale',58,6,'2026-04-14 12:10:00',NULL,NULL,'2026-04-14 12:05:00','2026-04-14 12:10:00'),(920022,10020,910006,'Adidas Ultraboost 42 码','黑白配色，跑步鞋','鞋底磨损轻微，已清洗，适合日常通勤慢跑。','Adidas','Ultraboost','good',2023,1299.00,360.00,1,'shipping','重庆','沙坪坝三峡广场',29.5628000,106.4581000,1,'on_sale',69,7,'2026-04-14 12:20:00',NULL,NULL,'2026-04-14 12:15:00','2026-04-14 12:20:00'),(920023,10010,910001,'任天堂 Switch OLED 白色','箱说齐全，附塞尔达卡带','屏幕贴膜，手柄无漂移，支持同城面交验机。','Nintendo','Switch OLED','good',2023,2599.00,1850.00,1,'both','深圳','会展中心地铁站',22.5409000,114.0596000,1,'on_sale',198,17,'2026-04-14 12:30:00',NULL,NULL,'2026-04-14 12:25:00','2026-04-14 12:30:00'),(920024,10011,910005,'MUJI 香薰机','带定时功能，白色款','办公室使用过几次，水箱干净，无异味。','MUJI','Aroma Diffuser','almost_new',2023,388.00,168.00,1,'shipping','成都','高新区天府三街',30.5431000,104.0660000,1,'on_sale',81,8,'2026-04-14 12:40:00',NULL,NULL,'2026-04-14 12:35:00','2026-04-14 12:40:00'),(920025,10012,910002,'明基 ScreenBar 屏幕挂灯','无频闪，Type-C 供电','亮度和色温调节正常，适合宿舍和办公室桌面。','BenQ','ScreenBar','good',2022,799.00,360.00,1,'shipping','武汉','光谷广场',30.5066000,114.4028000,1,'on_sale',92,11,'2026-04-14 12:50:00',NULL,NULL,'2026-04-14 12:45:00','2026-04-14 12:50:00'),(920026,10013,910010,'贝亲奶瓶消毒锅','蒸汽消毒，带烘干','使用半年左右，功能正常，已清洁。','Pigeon','Steam Sterilizer','good',2022,499.00,180.00,1,'both','深圳','龙华壹方天地',22.6590000,114.0263000,1,'on_sale',57,4,'2026-04-14 13:00:00',NULL,NULL,'2026-04-14 12:55:00','2026-04-14 13:00:00'),(920027,10014,910008,'飞利浦电动牙刷 HX6730','附两支未拆刷头','主机功能正常，刷头为全新独立包装。','Philips','HX6730','good',2021,399.00,120.00,0,'shipping','苏州','观前街',31.3134000,120.6217000,1,'on_sale',66,5,'2026-04-14 13:10:00',NULL,NULL,'2026-04-14 13:05:00','2026-04-14 13:10:00'),(920028,10015,910004,'Wilson 网球拍 Blade','附拍包，线近期刚换','拍框轻微掉漆，不影响使用，适合进阶练习。','Wilson','Blade 98','good',2021,1399.00,520.00,1,'both','南京','奥体中心东门',32.0102000,118.7217000,1,'on_sale',102,9,'2026-04-14 13:20:00',NULL,NULL,'2026-04-14 13:15:00','2026-04-14 13:20:00'),(920029,10016,910009,'卡西欧电子琴 CT-S200','61键，带谱架','音色和节奏功能正常，适合初学练习，支持自提。','Casio','CT-S200','good',2020,999.00,430.00,1,'pickup','西安','钟楼地铁站',34.2610000,108.9420000,1,'on_sale',74,6,'2026-04-14 13:30:00',NULL,NULL,'2026-04-14 13:25:00','2026-04-14 13:30:00'),(920030,10017,910001,'佳能 EF 50mm F1.8 STM 镜头','小痰盂，成像正常','镜片通透，无霉无雾，外壳正常使用痕迹。','Canon','EF 50mm F1.8 STM','good',2021,899.00,520.00,1,'shipping','长沙','五一广场',28.1950000,112.9768000,1,'off_shelf',120,10,'2026-04-14 13:40:00','2026-04-15 09:00:00',NULL,'2026-04-14 13:35:00','2026-04-15 09:00:00');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_favorite`
--

DROP TABLE IF EXISTS `product_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_favorite` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint unsigned NOT NULL COMMENT '买家ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_favorite_user_product` (`user_id`,`product_id`),
  KEY `idx_product_favorite_product` (`product_id`),
  CONSTRAINT `fk_product_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_product_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_favorite`
--

LOCK TABLES `product_favorite` WRITE;
/*!40000 ALTER TABLE `product_favorite` DISABLE KEYS */;
INSERT INTO `product_favorite` VALUES (1,10001,920003,'2026-04-14 11:00:00'),(2,10001,920008,'2026-04-14 11:01:00'),(3,10002,920004,'2026-04-14 11:02:00'),(4,10002,920010,'2026-04-14 11:03:00'),(5,10005,920009,'2026-04-14 11:04:00'),(6,10008,920006,'2026-04-14 11:05:00'),(7,10009,920004,'2026-04-14 11:06:00'),(8,10009,920008,'2026-04-14 11:07:00'),(9,10010,920020,'2026-04-14 11:08:00'),(10,10011,920012,'2026-04-14 11:09:00'),(11,10012,920023,'2026-04-14 11:10:00'),(12,10013,920018,'2026-04-14 11:11:00'),(13,10014,920025,'2026-04-14 11:12:00'),(14,10015,920013,'2026-04-14 11:13:00'),(15,10016,920028,'2026-04-14 11:14:00'),(16,10017,920020,'2026-04-14 11:15:00'),(17,10018,920014,'2026-04-14 11:16:00'),(18,10019,920012,'2026-04-14 11:17:00'),(19,10020,920027,'2026-04-14 11:18:00'),(20,10009,920023,'2026-04-14 11:19:00'),(21,10001,920018,'2026-04-14 11:20:00'),(22,10002,920021,'2026-04-14 11:21:00'),(23,10005,920024,'2026-04-14 11:22:00'),(24,10008,920022,'2026-04-14 11:23:00');
/*!40000 ALTER TABLE `product_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品图片ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `is_cover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否封面图：0否1是',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_image_product` (`product_id`),
  KEY `idx_product_image_cover` (`product_id`,`is_cover`),
  CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=930004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表（多图）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (930001,920001,'product/900001/2c6a7d29-491e-455f-bd0f-126cc7ba6f37.webp',1,1,'2026-04-12 22:26:20'),(930002,920002,'product/900001/1c4b716f-d594-4c08-90d6-4cb7a9c3245c.webp',1,1,'2026-04-12 22:26:20'),(930003,920003,'product/900002/1f94253a-85c9-4c52-9a10-4f75bf2b993a.webp',1,1,'2026-04-12 22:26:20'),(930004,920004,'product/900001/ea658b9e-7feb-4c5e-aaa0-16dba643eae6.webp',1,1,'2026-04-14 09:10:00'),(930005,920004,'product/900001/ea658b9e-7feb-4c5e-aaa0-16dba643eae6.webp',0,2,'2026-04-14 09:10:00'),(930006,920005,'product/900001/30cd7b21-8e0f-44ab-a63c-ac2b8e306fb5.webp',1,1,'2026-04-14 09:20:00'),(930007,920005,'product/900001/30cd7b21-8e0f-44ab-a63c-ac2b8e306fb5.webp',0,2,'2026-04-14 09:20:00'),(930008,920006,'product/900002/f48443bc-63f9-4d64-bde0-304fdcf4e6f2.webp',1,1,'2026-04-14 09:30:00'),(930009,920007,'product/900002/92eb6373-9cc2-4caf-b2f8-2b3180c58afd.webp',1,1,'2026-04-14 09:40:00'),(930010,920007,'product/900002/92eb6373-9cc2-4caf-b2f8-2b3180c58afd.webp',0,2,'2026-04-14 09:40:00'),(930011,920008,'product/10003/3bbc62ae-b8f2-409f-83c4-f1cf1f022a19.webp',1,1,'2026-04-14 10:00:00'),(930012,920008,'product/10003/3bbc62ae-b8f2-409f-83c4-f1cf1f022a19.webp',0,2,'2026-04-14 10:00:00'),(930013,920009,'product/10007/0c0b8995-8096-4a06-9a1e-d793c0972c35.webp',1,1,'2026-04-14 10:10:00'),(930014,920009,'product/10007/0c0b8995-8096-4a06-9a1e-d793c0972c35.webp',0,2,'2026-04-14 10:10:00'),(930015,920009,'product/10007/0c0b8995-8096-4a06-9a1e-d793c0972c35.webp',0,3,'2026-04-14 10:10:00'),(930016,920010,'product/10005/03c04e5c-8e63-43bd-9242-da8f773cc054.webp',1,1,'2026-04-14 10:20:00'),(930017,920011,'product/10003/3cd29f3d-f86d-47f8-8974-4aa1d35b0f68.webp',1,1,'2026-04-14 10:30:00'),(930018,920012,'product/10010/5ef0e694-2469-4d14-a17a-bf85a904b81b.webp',1,1,'2026-04-14 10:40:00'),(930019,920012,'product/10010/5ef0e694-2469-4d14-a17a-bf85a904b81b.webp',0,2,'2026-04-14 10:40:00'),(930020,920013,'product/10011/021edf24-fbc2-48ed-81ff-57cafd72df00.webp',1,1,'2026-04-14 10:50:00'),(930021,920014,'product/10012/ab54a065-0523-4eb6-ada7-2983bff2cf9e.png',1,1,'2026-04-14 11:00:00'),(930022,920014,'product/10012/ab54a065-0523-4eb6-ada7-2983bff2cf9e.png',0,2,'2026-04-14 11:00:00'),(930023,920015,'product/10013/5bd8dfe8-fcc9-4409-94cd-5df0cf565efc.webp',1,1,'2026-04-14 11:10:00'),(930024,920016,'product/10014/36139ea5-d3c3-4d00-b14c-ba373900788c.webp',1,1,'2026-04-14 11:20:00'),(930025,920017,'product/10015/24c60a0a-6eb3-4f2f-854f-3e9226f25a2b.webp',1,1,'2026-04-14 11:30:00'),(930026,920018,'product/10016/12cba046-61bb-4b0a-897c-01472cbfd780.png',1,1,'2026-04-14 11:40:00'),(930027,920018,'product/10016/12cba046-61bb-4b0a-897c-01472cbfd780.png',0,2,'2026-04-14 11:40:00'),(930028,920019,'product/10017/a04f0b86-9910-4c2b-857a-067442377177.webp',1,1,'2026-04-14 11:50:00'),(930029,920020,'product/10018/79366839-1ed4-45fc-acb5-fe729c7b172b.webp',1,1,'2026-04-14 12:00:00'),(930030,920020,'product/10018/79366839-1ed4-45fc-acb5-fe729c7b172b.webp',0,2,'2026-04-14 12:00:00'),(930031,920021,'product/10019/32940d5b-e87e-450d-b20a-fa929aaa8518.webp',1,1,'2026-04-14 12:10:00'),(930032,920022,'product/10020/a3b2e68f-36e9-46fa-a695-78c2f3d19342.webp',1,1,'2026-04-14 12:20:00'),(930033,920023,'product/10010/95d0f9a0-cc43-4de7-b142-867e5f9086ad.webp',1,1,'2026-04-14 12:30:00'),(930034,920023,'product/10010/95d0f9a0-cc43-4de7-b142-867e5f9086ad.webp',0,2,'2026-04-14 12:30:00'),(930035,920024,'product/10011/9f394789-0d18-4b7c-a47f-b18ee4d6297c.webp',1,1,'2026-04-14 12:40:00'),(930036,920025,'product/10012/48b20ec2-1824-4033-ba35-c8ddd3db6f17.webp',1,1,'2026-04-14 12:50:00'),(930037,920026,'product/10013/8c7f7885-49e1-42d6-bb60-8f318a491a1d.webp',1,1,'2026-04-14 13:00:00'),(930038,920027,'product/10014/a8e41bca-19a8-44cb-a1b1-3b33be7ffe9f.webp',1,1,'2026-04-14 13:10:00'),(930039,920028,'product/10015/8938d8c2-161c-462b-b77e-4eb88ced43c1.jpg',1,1,'2026-04-14 13:20:00'),(930040,920028,'product/10015/8938d8c2-161c-462b-b77e-4eb88ced43c1.jpg',0,2,'2026-04-14 13:20:00'),(930041,920029,'product/10016/8dd8e74b-c199-48d9-a279-981430321d71.gif',1,1,'2026-04-14 13:30:00'),(930042,920030,'product/10017/56667b26-a66a-4bd7-bb71-6a67f398e007.webp',1,1,'2026-04-14 13:40:00'),(930043,920011,'product/10003/35446369-25be-48e0-b0d9-30d1b5863a09.webp',0,2,'2026-04-14 10:30:00');
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_item_id` bigint unsigned NOT NULL COMMENT '订单明细ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `buyer_id` bigint unsigned NOT NULL COMMENT '买家ID（评价人）',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家ID（被评人）',
  `rating` tinyint unsigned NOT NULL COMMENT '评分（1-5）',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名评价',
  `has_sensitive_content` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否命中敏感内容',
  `seller_reply` varchar(1000) DEFAULT NULL COMMENT '卖家回复',
  `seller_reply_at` datetime DEFAULT NULL COMMENT '卖家回复时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_order_item` (`order_item_id`),
  KEY `idx_review_seller` (`seller_id`,`created_at`),
  KEY `idx_review_buyer` (`buyer_id`,`created_at`),
  KEY `idx_review_product` (`product_id`),
  KEY `fk_review_order` (`order_id`),
  CONSTRAINT `fk_review_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_review_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`),
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_review_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `chk_review_rating` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,940006,950006,920003,10009,900002,5,'成色很好，描述一致',0,0,'感谢支持，后续有问题可以随时联系。','2026-04-13 13:15:22','2026-04-13 13:11:41');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_image`
--

DROP TABLE IF EXISTS `review_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_image` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评价图片ID',
  `review_id` bigint unsigned NOT NULL COMMENT '评价ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_image_review` (`review_id`),
  CONSTRAINT `fk_review_image_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_image`
--

LOCK TABLES `review_image` WRITE;
/*!40000 ALTER TABLE `review_image` DISABLE KEYS */;
INSERT INTO `review_image` VALUES (1,1,'/uploads/reviews/review_10001_1.jpg',0,'2026-04-13 13:14:40');
/*!40000 ALTER TABLE `review_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seller_follow`
--

DROP TABLE IF EXISTS `seller_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seller_follow` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `buyer_id` bigint unsigned NOT NULL COMMENT '买家ID（关注者）',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家ID（被关注者）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_follow_buyer_seller` (`buyer_id`,`seller_id`),
  KEY `idx_seller_follow_seller` (`seller_id`),
  CONSTRAINT `fk_seller_follow_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_seller_follow_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='卖家关注表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seller_follow`
--

LOCK TABLES `seller_follow` WRITE;
/*!40000 ALTER TABLE `seller_follow` DISABLE KEYS */;
INSERT INTO `seller_follow` VALUES (1,10001,900001,'2026-04-14 11:20:00'),(2,10001,900002,'2026-04-14 11:21:00'),(3,10002,10003,'2026-04-14 11:22:00'),(4,10005,10007,'2026-04-14 11:23:00'),(5,10008,900002,'2026-04-14 11:24:00'),(6,10009,900001,'2026-04-14 11:25:00'),(7,10009,10003,'2026-04-14 11:26:00'),(8,10010,10018,'2026-04-14 11:27:00'),(9,10011,10010,'2026-04-14 11:28:00'),(10,10012,10007,'2026-04-14 11:29:00'),(11,10013,10016,'2026-04-14 11:30:00'),(12,10014,10012,'2026-04-14 11:31:00'),(13,10015,10020,'2026-04-14 11:32:00'),(14,10016,10015,'2026-04-14 11:33:00'),(15,10017,10014,'2026-04-14 11:34:00'),(16,10018,10012,'2026-04-14 11:35:00'),(17,10019,10010,'2026-04-14 11:36:00'),(18,10020,10013,'2026-04-14 11:37:00');
/*!40000 ALTER TABLE `seller_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seller_reputation_snapshot`
--

DROP TABLE IF EXISTS `seller_reputation_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seller_reputation_snapshot` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '快照ID，主键，自增',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家ID，外键 user_account.id',
  `snapshot_date` date NOT NULL COMMENT '快照日期，与 seller_id 组成唯一约束',
  `credit_score` int NOT NULL COMMENT '当日信用分',
  `positive_rate` decimal(5,2) NOT NULL COMMENT '当日好评率',
  `total_orders` int NOT NULL DEFAULT '0' COMMENT '累计订单数，默认0',
  `completed_orders` int NOT NULL DEFAULT '0' COMMENT '完成订单数，默认0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_date` (`seller_id`,`snapshot_date`),
  CONSTRAINT `fk_seller_reputation_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_credit_score_snapshot` CHECK ((`credit_score` >= 0)),
  CONSTRAINT `chk_positive_rate_snapshot` CHECK (((`positive_rate` >= 0) and (`positive_rate` <= 100)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卖家信誉日快照表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seller_reputation_snapshot`
--

LOCK TABLES `seller_reputation_snapshot` WRITE;
/*!40000 ALTER TABLE `seller_reputation_snapshot` DISABLE KEYS */;
INSERT INTO `seller_reputation_snapshot` VALUES (1,900001,'2026-04-12',96,100.00,4,1,'2026-04-12 23:00:00'),(2,900002,'2026-04-12',94,100.00,5,1,'2026-04-12 23:00:00'),(3,10003,'2026-04-12',92,100.00,1,0,'2026-04-12 23:00:00'),(4,10007,'2026-04-12',98,100.00,0,0,'2026-04-12 23:00:00'),(5,900001,'2026-04-13',96,100.00,5,1,'2026-04-13 23:00:00'),(6,900002,'2026-04-13',95,100.00,6,2,'2026-04-13 23:00:00'),(7,10003,'2026-04-13',92,100.00,1,0,'2026-04-13 23:00:00'),(8,10007,'2026-04-13',98,100.00,0,0,'2026-04-13 23:00:00'),(9,900001,'2026-04-14',96,100.00,5,1,'2026-04-14 23:00:00'),(10,900002,'2026-04-14',95,100.00,6,2,'2026-04-14 23:00:00'),(11,10003,'2026-04-14',93,100.00,2,0,'2026-04-14 23:00:00'),(12,10007,'2026-04-14',98,100.00,1,0,'2026-04-14 23:00:00'),(13,10010,'2026-04-14',97,99.20,12,9,'2026-04-14 23:00:00'),(14,10011,'2026-04-14',91,96.80,8,6,'2026-04-14 23:00:00'),(15,10012,'2026-04-14',95,98.50,10,7,'2026-04-14 23:00:00'),(16,10013,'2026-04-14',89,94.00,6,4,'2026-04-14 23:00:00'),(17,10014,'2026-04-14',93,97.00,7,5,'2026-04-14 23:00:00'),(18,10015,'2026-04-14',90,95.50,5,3,'2026-04-14 23:00:00'),(19,10016,'2026-04-14',96,100.00,9,7,'2026-04-14 23:00:00'),(20,10017,'2026-04-14',92,96.00,6,4,'2026-04-14 23:00:00'),(21,10018,'2026-04-14',94,98.00,11,8,'2026-04-14 23:00:00'),(22,10019,'2026-04-14',88,93.50,4,2,'2026-04-14 23:00:00'),(23,10020,'2026-04-14',91,95.00,5,3,'2026-04-14 23:00:00');
/*!40000 ALTER TABLE `seller_reputation_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trade_order`
--

DROP TABLE IF EXISTS `trade_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号（业务唯一）',
  `buyer_id` bigint unsigned NOT NULL COMMENT '买家用户ID（来自user_account）',
  `seller_id` bigint unsigned NOT NULL COMMENT '卖家用户ID（来自user_account）',
  `order_status` enum('pending_payment','paid_pending_ship','shipped','delivered','completed','cancelled','refund_in_progress','closed') NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态',
  `trade_mode` enum('pickup','shipping') NOT NULL COMMENT '本订单交易方式',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额（元）',
  `freight_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费金额（元）',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额（元）',
  `remark` varchar(255) DEFAULT NULL COMMENT '买家备注',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名（邮寄）',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话（邮寄）',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货地址（邮寄）',
  `pickup_location` varchar(255) DEFAULT NULL COMMENT '自提地点（自提）',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `paid_at` datetime DEFAULT NULL COMMENT '支付完成时间',
  `shipped_at` datetime DEFAULT NULL COMMENT '发货时间',
  `delivered_at` datetime DEFAULT NULL COMMENT '签收时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间（确认收货）',
  `cancelled_at` datetime DEFAULT NULL COMMENT '取消时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_order_no` (`order_no`),
  KEY `idx_trade_order_buyer` (`buyer_id`,`created_at`),
  KEY `idx_trade_order_seller` (`seller_id`,`created_at`),
  KEY `idx_trade_order_status` (`order_status`),
  CONSTRAINT `fk_trade_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_trade_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `chk_trade_order_amount` CHECK (((`total_amount` >= 0) and (`freight_amount` >= 0) and (`pay_amount` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=940010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trade_order`
--

LOCK TABLES `trade_order` WRITE;
/*!40000 ALTER TABLE `trade_order` DISABLE KEYS */;
INSERT INTO `trade_order` VALUES (940001,'ODSEED202604120001',10001,900001,'pending_payment','shipping',2999.00,12.00,3011.00,'请工作日傍晚送达','张三','13800000001','上海市徐汇区南丹东路1号',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-12 10:00:00','2026-04-12 10:00:00'),(940002,'ODSEED202604120002',10001,900001,'shipped','shipping',3499.00,0.00,3499.00,'请包装严实一些','张三','13800000001','上海市徐汇区天钥桥路2号',NULL,NULL,'2026-04-12 10:10:00','2026-04-12 11:00:00',NULL,NULL,NULL,'2026-04-12 10:05:00','2026-04-12 11:00:00'),(940003,'ODSEED202604120003',10001,900001,'cancelled','pickup',2999.00,0.00,2999.00,'临时改为下周再看',NULL,NULL,NULL,'徐家汇地铁站2号口','买家临时取消',NULL,NULL,NULL,NULL,'2026-04-12 12:00:00','2026-04-12 11:30:00','2026-04-12 12:00:00'),(940004,'OD202604122227494620',10009,900001,'pending_payment','shipping',2999.00,12.00,3011.00,'尽快发货','张三','13800000001','上海市徐汇区漕溪北路100号',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-12 22:27:49','2026-04-12 22:27:49'),(940005,'OD202604122237579036',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','李四','13800000023','广东省深圳市南山区粤海大学',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-12 22:39:23','2026-04-12 22:37:57','2026-04-12 22:37:57'),(940006,'OD202604130025426622',10009,900002,'completed','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,NULL,'2026-04-13 12:05:37','2026-04-13 12:27:05','2026-04-13 12:33:22','2026-04-13 13:03:18',NULL,'2026-04-13 00:25:42','2026-04-13 00:25:42'),(940007,'OD202604130025525935',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-13 00:26:38','2026-04-13 00:25:52','2026-04-13 00:25:52'),(940008,'OD202604131154339875',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-13 11:56:04','2026-04-13 11:54:33','2026-04-13 11:54:33'),(940009,'OD202604131236575883',10009,900002,'delivered','pickup',1798.00,15.00,1813.00,'尽快发货',NULL,NULL,NULL,'深圳大学沧海致腾楼240',NULL,'2026-04-13 12:38:37','2026-04-13 12:40:53','2026-04-13 13:01:30',NULL,NULL,'2026-04-13 12:36:57','2026-04-13 12:36:57');
/*!40000 ALTER TABLE `trade_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（平台唯一）',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号，唯一约束，可空',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱，唯一约束，可空',
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希值',
  `can_buy` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否具备买家能力，默认1，约束0/1',
  `can_sell` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否具备卖家能力，默认1，约束0/1',
  `is_admin` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否管理员，默认0，约束0/1',
  `user_status` enum('pending','active','banned') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '用户状态',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间，可空',
  `registered_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间，默认当前时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  CONSTRAINT `chk_can_buy` CHECK ((`can_buy` in (0,1))),
  CONSTRAINT `chk_can_sell` CHECK ((`can_sell` in (0,1))),
  CONSTRAINT `chk_is_admin` CHECK ((`is_admin` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=900004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_account`
--

LOCK TABLES `user_account` WRITE;
/*!40000 ALTER TABLE `user_account` DISABLE KEYS */;
INSERT INTO `user_account` VALUES (1,'admin','admin','13800000000','admin@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,1,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10001,'张三','张三','13800000001','zhangsan@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,'李四','李四','13800000002','lisi@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,'王五','王五','13800000003','wangwu@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,'赵六','赵六','13800000004','zhaoliu@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,'小明','小明','13800000005','xiaoming@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,'小红','小红','13800000006','xiaohong@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'banned',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,'大刘','大刘','13800000007','daliu@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,'小陈','小陈','13800000008','xiaochen@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,'lin_muqing','林慕青','13990009999','lin.muqing@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'pending','2026-04-13 13:27:07','2026-04-12 22:23:21','2026-04-12 22:23:21','2026-04-13 13:27:07'),(10010,'chen_yifan','陈一帆','13990000010','chen.yifan@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 18:20:00','2026-04-14 08:00:00','2026-04-14 08:00:00','2026-04-14 18:20:00'),(10011,'su_xiaoman','苏小满','13990000011','su.xiaoman@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 17:12:00','2026-04-14 08:02:00','2026-04-14 08:02:00','2026-04-14 17:12:00'),(10012,'zhou_yuan','周远','13990000012','zhou.yuan@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 16:35:00','2026-04-14 08:04:00','2026-04-14 08:04:00','2026-04-14 16:35:00'),(10013,'he_miaomiao','何淼淼','13990000013','he.miaomiao@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 16:00:00','2026-04-14 08:06:00','2026-04-14 08:06:00','2026-04-14 16:00:00'),(10014,'wu_qian','吴倩','13990000014','wu.qian@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 15:46:00','2026-04-14 08:08:00','2026-04-14 08:08:00','2026-04-14 15:46:00'),(10015,'luo_hao','罗浩','13990000015','luo.hao@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 15:10:00','2026-04-14 08:10:00','2026-04-14 08:10:00','2026-04-14 15:10:00'),(10016,'tang_xinyi','唐欣怡','13990000016','tang.xinyi@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 14:50:00','2026-04-14 08:12:00','2026-04-14 08:12:00','2026-04-14 14:50:00'),(10017,'gao_rui','高瑞','13990000017','gao.rui@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 14:22:00','2026-04-14 08:14:00','2026-04-14 08:14:00','2026-04-14 14:22:00'),(10018,'xia_yue','夏悦','13990000018','xia.yue@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 13:58:00','2026-04-14 08:16:00','2026-04-14 08:16:00','2026-04-14 13:58:00'),(10019,'feng_ke','冯可','13990000019','feng.ke@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 13:30:00','2026-04-14 08:18:00','2026-04-14 08:18:00','2026-04-14 13:30:00'),(10020,'qiao_nan','乔楠','13990000020','qiao.nan@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-14 13:02:00','2026-04-14 08:20:00','2026-04-14 08:20:00','2026-04-14 13:02:00'),(900001,'shanghai_digital','海川数码','13990000001','haichuan.digital@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active',NULL,'2026-04-12 22:26:20','2026-04-12 22:26:20','2026-04-12 22:26:20'),(900002,'beijing_life','京北闲置','13990000002','jingbei.life@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'active','2026-04-13 13:25:53','2026-04-12 22:26:20','2026-04-12 22:26:20','2026-04-13 13:25:53'),(900003,'platform_admin','平台管理员','13900000099','platform.admin@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,1,'active','2026-04-13 13:40:39','2026-04-13 00:44:30','2026-04-13 00:44:30','2026-04-13 13:40:39');
/*!40000 ALTER TABLE `user_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键，自增',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID，外键 user_account.id',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址，0-否，1-是，默认0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user_address_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_is_default` CHECK ((`is_default` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,10001,'张三','13800000001','上海市','上海市','徐汇区','漕溪北路100号1幢301室',1,'2026-04-14 08:30:00','2026-04-14 08:30:00'),(2,10001,'张三','13800000001','上海市','上海市','杨浦区','大学路88号学生公寓5号楼',0,'2026-04-14 08:31:00','2026-04-14 08:31:00'),(3,10002,'李四','13800000002','北京市','北京市','海淀区','中关村大街59号教学楼A座',1,'2026-04-14 08:32:00','2026-04-14 08:32:00'),(4,10009,'林慕青','13990009999','广东省','深圳市','南山区','粤海街道科技园创客中心240室',1,'2026-04-14 08:33:00','2026-04-14 08:33:00'),(5,10009,'林慕青','13990009999','上海市','上海市','徐汇区','虹桥路1号港汇广场B座',0,'2026-04-14 08:34:00','2026-04-14 08:34:00'),(6,10005,'小明','13800000005','江苏省','南京市','栖霞区','仙林大道163号快递中心',1,'2026-04-14 08:35:00','2026-04-14 08:35:00'),(7,10010,'陈一帆','13990000010','广东省','深圳市','南山区','高新南一道软件产业基地2栋',1,'2026-04-14 08:36:00','2026-04-14 08:36:00'),(8,10011,'苏小满','13990000011','四川省','成都市','锦江区','红星路三段春熙路商圈',1,'2026-04-14 08:37:00','2026-04-14 08:37:00'),(9,10012,'周远','13990000012','湖北省','武汉市','洪山区','珞喻路街道口未来城',1,'2026-04-14 08:38:00','2026-04-14 08:38:00'),(10,10013,'何淼淼','13990000013','广东省','深圳市','宝安区','宝安中心区海秀路',1,'2026-04-14 08:39:00','2026-04-14 08:39:00'),(11,10014,'吴倩','13990000014','江苏省','苏州市','工业园区','星湖街328号创意产业园',1,'2026-04-14 08:40:00','2026-04-14 08:40:00'),(12,10015,'罗浩','13990000015','江苏省','南京市','建邺区','奥体大街68号',1,'2026-04-14 08:41:00','2026-04-14 08:41:00'),(13,10016,'唐欣怡','13990000016','陕西省','西安市','雁塔区','小寨东路126号',1,'2026-04-14 08:42:00','2026-04-14 08:42:00'),(14,10017,'高瑞','13990000017','湖南省','长沙市','岳麓区','麓山南路大学城',1,'2026-04-14 08:43:00','2026-04-14 08:43:00'),(15,10018,'夏悦','13990000018','广东省','广州市','天河区','珠江新城华夏路',1,'2026-04-14 08:44:00','2026-04-14 08:44:00'),(16,10019,'冯可','13990000019','天津市','天津市','南开区','卫津路94号',1,'2026-04-14 08:45:00','2026-04-14 08:45:00'),(17,10020,'乔楠','13990000020','重庆市','重庆市','沙坪坝区','三峡广场步行街',1,'2026-04-14 08:46:00','2026-04-14 08:46:00');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile` (
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID，主键，外键 user_account.id',
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像URL，可空',
  `gender` enum('unknown','male','female') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknown' COMMENT '性别',
  `birthday` date DEFAULT NULL COMMENT '生日，可空',
  `bio` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介，可空',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在城市，可空',
  `district` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在区县，可空',
  `credit_score` int NOT NULL DEFAULT '100' COMMENT '平台信用分，默认100，最小值0',
  `positive_rate` decimal(5,2) NOT NULL DEFAULT '100.00' COMMENT '好评率，默认100.00，范围0~100',
  `total_review_count` int NOT NULL DEFAULT '0' COMMENT '累计评价数，默认0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_profile_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_credit_score` CHECK ((`credit_score` >= 0)),
  CONSTRAINT `chk_positive_rate` CHECK (((`positive_rate` >= 0) and (`positive_rate` <= 100)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展资料与信誉表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile`
--

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;
INSERT INTO `user_profile` VALUES (10001,'/avatars/zhangsan.jpg','unknown',NULL,'Digital goods enthusiast, trusted trader',NULL,NULL,95,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,'/avatars/lisi.jpg','unknown',NULL,'Student buyer looking for used textbooks',NULL,NULL,88,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,'/avatars/wangwu.jpg','unknown',NULL,'Professional seller with fair prices',NULL,NULL,92,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,'/avatars/zhaoliu.jpg','unknown',NULL,'New to the platform, nice to meet you',NULL,NULL,75,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,'/avatars/xiaoming.jpg','unknown',NULL,'Enjoys hunting for second-hand deals',NULL,NULL,85,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,'/avatars/xiaohong.jpg','unknown',NULL,'Focuses on sharing mother-and-baby products',NULL,NULL,60,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,'/avatars/daliu.jpg','unknown',NULL,'Photography lover and gear collector',NULL,NULL,98,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,'/avatars/xiaochen.jpg','unknown',NULL,'Bargain hunter',NULL,NULL,82,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,'/avatars/lin_muqing.jpg','female','1999-05-12','喜欢收集实用数码和书籍，偏好同城面交。','深圳','南山区',100,100.00,0,'2026-04-12 22:23:21','2026-04-14 08:33:00'),(10010,'/avatars/chen_yifan.jpg','male','1997-02-18','数码产品爱好者，出闲置会写清楚成色。','深圳','南山区',97,99.20,9,'2026-04-14 08:00:00','2026-04-14 08:00:00'),(10011,'/avatars/su_xiaoman.jpg','female','1998-08-09','护肤和家居小物较多，支持邮寄。','成都','锦江区',91,96.80,6,'2026-04-14 08:02:00','2026-04-14 08:02:00'),(10012,'/avatars/zhou_yuan.jpg','male','1996-11-23','喜欢吉他和桌面设备，面交可验货。','武汉','洪山区',95,98.50,7,'2026-04-14 08:04:00','2026-04-14 08:04:00'),(10013,'/avatars/he_miaomiao.jpg','female','1995-03-30','家有宝宝，主要转让母婴闲置。','深圳','宝安区',89,94.00,4,'2026-04-14 08:06:00','2026-04-14 08:06:00'),(10014,'/avatars/wu_qian.jpg','female','1999-12-01','衣物和个护用品会先清洁再发出。','苏州','工业园区',93,97.00,5,'2026-04-14 08:08:00','2026-04-14 08:08:00'),(10015,'/avatars/luo_hao.jpg','male','1994-06-17','搬家清理家具和运动装备。','南京','建邺区',90,95.50,3,'2026-04-14 08:10:00','2026-04-14 08:10:00'),(10016,'/avatars/tang_xinyi.jpg','female','2000-09-21','平板、乐器和学习用品偶尔出闲置。','西安','雁塔区',96,100.00,7,'2026-04-14 08:12:00','2026-04-14 08:12:00'),(10017,'/avatars/gao_rui.jpg','male','1998-04-05','运动用品和摄影配件为主。','长沙','岳麓区',92,96.00,4,'2026-04-14 08:14:00','2026-04-14 08:14:00'),(10018,'/avatars/xia_yue.jpg','female','1997-07-14','家电小物都支持当面试用。','广州','天河区',94,98.00,8,'2026-04-14 08:16:00','2026-04-14 08:16:00'),(10019,'/avatars/feng_ke.jpg','male','2001-01-08','备考资料和书籍比较多。','天津','南开区',88,93.50,2,'2026-04-14 08:18:00','2026-04-14 08:18:00'),(10020,'/avatars/qiao_nan.jpg','female','1998-10-26','鞋包和日用小物，发货前会拍照确认。','重庆','沙坪坝区',91,95.00,3,'2026-04-14 08:20:00','2026-04-14 08:20:00'),(900001,'/avatars/haichuan_digital.jpg','unknown',NULL,'长期整理个人数码闲置，支持当面验机。','上海','徐汇区',96,100.00,0,'2026-04-12 22:26:20','2026-04-14 09:00:00'),(900002,'/avatars/jingbei_life.jpg','unknown',NULL,'北京本地闲置，家居和数码配件较多。','北京','朝阳区',94,100.00,0,'2026-04-12 22:26:20','2026-04-14 09:00:00'),(900003,NULL,'unknown',NULL,'平台运营管理员。',NULL,NULL,100,100.00,0,'2026-04-13 00:44:30','2026-04-13 00:44:30');
/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_verification`
--

DROP TABLE IF EXISTS `user_verification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_verification` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '认证记录ID，主键，自增',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID，外键 user_account.id',
  `verify_type` enum('real_name','student','merchant') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '认证类型',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名（仅实名认证时填写），可空',
  `id_card_number` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号码（加密存储，仅实名认证时填写），可空',
  `verify_status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '认证状态',
  `submitted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间，默认当前时间',
  `reviewed_by` bigint unsigned DEFAULT NULL COMMENT '审核管理员ID，外键 user_account.id，可空',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间，可空',
  `reject_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '驳回原因，可空',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，默认当前时间，自动更新',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_verify_status` (`verify_status`),
  KEY `fk_user_verification_reviewed_by` (`reviewed_by`),
  CONSTRAINT `fk_user_verification_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `user_account` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_verification_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证审核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_verification`
--

LOCK TABLES `user_verification` WRITE;
/*!40000 ALTER TABLE `user_verification` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_verification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_forum_post_detail`
--

DROP TABLE IF EXISTS `v_forum_post_detail`;
/*!50001 DROP VIEW IF EXISTS `v_forum_post_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_forum_post_detail` AS SELECT 
 1 AS `id`,
 1 AS `author_id`,
 1 AS `category_id`,
 1 AS `post_type`,
 1 AS `product_id`,
 1 AS `title`,
 1 AS `content`,
 1 AS `is_deleted`,
 1 AS `audit_status`,
 1 AS `display_status`,
 1 AS `like_count`,
 1 AS `comment_count`,
 1 AS `share_count`,
 1 AS `collect_count`,
 1 AS `view_count`,
 1 AS `last_commented_at`,
 1 AS `published_at`,
 1 AS `reject_reason`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `author_name`,
 1 AS `author_avatar`,
 1 AS `author_credit_score`,
 1 AS `author_bio`,
 1 AS `author_join_time`,
 1 AS `author_post_count`,
 1 AS `tag_follow_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_forum_post_list`
--

DROP TABLE IF EXISTS `v_forum_post_list`;
/*!50001 DROP VIEW IF EXISTS `v_forum_post_list`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_forum_post_list` AS SELECT 
 1 AS `id`,
 1 AS `title`,
 1 AS `content`,
 1 AS `post_type`,
 1 AS `like_count`,
 1 AS `comment_count`,
 1 AS `share_count`,
 1 AS `collect_count`,
 1 AS `view_count`,
 1 AS `published_at`,
 1 AS `created_at`,
 1 AS `audit_status`,
 1 AS `display_status`,
 1 AS `author_id`,
 1 AS `author_name`,
 1 AS `author_avatar`,
 1 AS `author_credit_score`,
 1 AS `tags`,
 1 AS `category_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `wallet_account`
--

DROP TABLE IF EXISTS `wallet_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '钱包账户ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID（卖家）',
  `account_status` enum('active','frozen','closed') NOT NULL DEFAULT 'active' COMMENT '账户状态',
  `available_balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '可用余额（元）',
  `frozen_balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '冻结余额（元）',
  `total_income` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计收入（元）',
  `total_withdraw` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计提现（元）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wallet_account_user` (`user_id`),
  CONSTRAINT `fk_wallet_account_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `chk_wallet_account_balance` CHECK (((`available_balance` >= 0) and (`frozen_balance` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='钱包账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_account`
--

LOCK TABLES `wallet_account` WRITE;
/*!40000 ALTER TABLE `wallet_account` DISABLE KEYS */;
INSERT INTO `wallet_account` VALUES (1,10009,'active',3460.00,240.00,5000.00,1300.00,'2026-04-13 00:34:22','2026-04-13 00:36:27'),(2,10003,'active',1377.00,0.00,2800.00,650.00,'2026-04-13 00:36:27','2026-04-13 00:36:27'),(3,900001,'active',888.88,0.00,1200.00,311.12,'2026-04-13 00:36:27','2026-04-13 00:36:27'),(6,900003,'active',0.00,0.00,0.00,0.00,'2026-04-13 00:46:23','2026-04-13 00:46:23');
/*!40000 ALTER TABLE `wallet_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_ledger`
--

DROP TABLE IF EXISTS `wallet_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_ledger` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '账本流水ID',
  `wallet_account_id` bigint unsigned NOT NULL COMMENT '钱包账户ID',
  `biz_type` enum('order_income','refund_out','withdraw_freeze','withdraw_success','withdraw_reject','manual_adjust') NOT NULL COMMENT '业务类型',
  `biz_id` bigint unsigned DEFAULT NULL COMMENT '业务单据ID（订单/提现等）',
  `change_amount` decimal(12,2) NOT NULL COMMENT '变动金额（可正可负）',
  `balance_after` decimal(12,2) NOT NULL COMMENT '变动后可用余额',
  `frozen_after` decimal(12,2) NOT NULL COMMENT '变动后冻结余额',
  `note` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_wallet_ledger_wallet_time` (`wallet_account_id`,`created_at`),
  KEY `idx_wallet_ledger_biz` (`biz_type`,`biz_id`),
  CONSTRAINT `fk_wallet_ledger_wallet` FOREIGN KEY (`wallet_account_id`) REFERENCES `wallet_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='钱包账本流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_ledger`
--

LOCK TABLES `wallet_ledger` WRITE;
/*!40000 ALTER TABLE `wallet_ledger` DISABLE KEYS */;
INSERT INTO `wallet_ledger` VALUES (1,1,'order_income',NULL,700.00,3700.00,0.00,'seed:income:10009','2026-04-12 18:36:27'),(2,1,'withdraw_freeze',1,-200.00,3500.00,200.00,'seed:freeze:WD202604130001','2026-04-12 21:36:27'),(3,1,'withdraw_success',4,0.00,3500.00,200.00,'seed:success:WD202604130004','2026-04-12 22:36:27'),(4,2,'manual_adjust',NULL,1200.00,1200.00,150.00,'seed:manual:10003','2026-04-12 16:36:27'),(5,2,'withdraw_reject',3,120.00,1200.00,150.00,'seed:reject:WD202604130003','2026-04-12 00:36:27'),(6,2,'manual_adjust',NULL,88.50,1288.50,150.00,'运营补偿','2026-04-13 00:45:20'),(7,2,'manual_adjust',NULL,88.50,1377.00,150.00,'运营补偿','2026-04-13 00:45:33'),(8,1,'withdraw_freeze',5,-120.00,3380.00,320.00,'发起提现，冻结资金','2026-04-13 00:47:04'),(9,1,'withdraw_reject',1,200.00,3580.00,120.00,'资料不完整，请补充后重提','2026-04-13 00:54:21'),(10,2,'withdraw_success',2,0.00,1377.00,0.00,'提现打款成功','2026-04-13 00:57:00'),(11,1,'withdraw_freeze',6,-120.00,3460.00,240.00,'发起提现，冻结资金','2026-04-13 13:04:49');
/*!40000 ALTER TABLE `wallet_ledger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `withdrawal_request`
--

DROP TABLE IF EXISTS `withdrawal_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `withdrawal_request` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '提现申请ID',
  `withdrawal_no` varchar(64) NOT NULL COMMENT '提现单号（业务唯一）',
  `user_id` bigint unsigned NOT NULL COMMENT '申请用户ID（卖家）',
  `wallet_account_id` bigint unsigned NOT NULL COMMENT '钱包账户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额（元）',
  `fee_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '手续费（元）',
  `channel` enum('wechat','alipay','bank_card') NOT NULL COMMENT '提现渠道',
  `channel_account_mask` varchar(100) NOT NULL COMMENT '提现账号脱敏信息',
  `withdrawal_status` enum('pending','approved','rejected','processing','paid','failed') NOT NULL DEFAULT 'pending' COMMENT '提现状态',
  `reviewed_by` bigint unsigned DEFAULT NULL COMMENT '审核管理员ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `paid_at` datetime DEFAULT NULL COMMENT '打款时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '驳回原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdrawal_request_no` (`withdrawal_no`),
  KEY `idx_withdrawal_request_user` (`user_id`,`created_at`),
  KEY `idx_withdrawal_request_status` (`withdrawal_status`),
  KEY `fk_withdrawal_request_wallet` (`wallet_account_id`),
  KEY `fk_withdrawal_request_admin` (`reviewed_by`),
  CONSTRAINT `fk_withdrawal_request_admin` FOREIGN KEY (`reviewed_by`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_withdrawal_request_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_withdrawal_request_wallet` FOREIGN KEY (`wallet_account_id`) REFERENCES `wallet_account` (`id`),
  CONSTRAINT `chk_withdrawal_request_amount` CHECK (((`amount` > 0) and (`fee_amount` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='卖家提现申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `withdrawal_request`
--

LOCK TABLES `withdrawal_request` WRITE;
/*!40000 ALTER TABLE `withdrawal_request` DISABLE KEYS */;
INSERT INTO `withdrawal_request` VALUES (1,'WD202604130001',10009,1,200.00,0.00,'alipay','ali***@example.com','rejected',900003,'2026-04-13 00:54:21',NULL,'资料不完整，请补充后重提','2026-04-13 00:36:27','2026-04-13 00:36:27'),(2,'WD202604130002',10003,2,150.00,0.00,'wechat','wx_13****88','paid',1,'2026-04-12 22:36:27','2026-04-13 00:57:00',NULL,'2026-04-12 21:36:27','2026-04-13 00:36:27'),(3,'WD202604130003',10003,2,120.00,0.00,'bank_card','6222********1234','rejected',1,'2026-04-12 00:36:27',NULL,'资料不完整，请补充后重试','2026-04-12 00:36:27','2026-04-13 00:36:27'),(4,'WD202604130004',10009,1,180.00,0.00,'alipay','ali***@example.com','paid',1,'2026-04-12 19:36:27','2026-04-12 20:36:27',NULL,'2026-04-12 18:36:27','2026-04-13 00:36:27'),(5,'WD202604130047045369',10009,1,120.00,0.00,'alipay','ali***@example.com','approved',900003,'2026-04-13 00:53:49',NULL,NULL,'2026-04-13 00:47:04','2026-04-13 00:47:04'),(6,'WD202604131304494364',10009,1,120.00,0.00,'alipay','ali***@example.com','pending',NULL,NULL,NULL,NULL,'2026-04-13 13:04:49','2026-04-13 13:04:49');
/*!40000 ALTER TABLE `withdrawal_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_forum_post_detail`
--

/*!50001 DROP VIEW IF EXISTS `v_forum_post_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_forum_post_detail` AS select `p`.`id` AS `id`,`p`.`author_id` AS `author_id`,`p`.`category_id` AS `category_id`,`p`.`post_type` AS `post_type`,`p`.`product_id` AS `product_id`,`p`.`title` AS `title`,`p`.`content` AS `content`,`p`.`is_deleted` AS `is_deleted`,`p`.`audit_status` AS `audit_status`,`p`.`display_status` AS `display_status`,`p`.`like_count` AS `like_count`,`p`.`comment_count` AS `comment_count`,`p`.`share_count` AS `share_count`,`p`.`collect_count` AS `collect_count`,`p`.`view_count` AS `view_count`,`p`.`last_commented_at` AS `last_commented_at`,`p`.`published_at` AS `published_at`,`p`.`reject_reason` AS `reject_reason`,`p`.`created_at` AS `created_at`,`p`.`updated_at` AS `updated_at`,`ua`.`username` AS `author_name`,`up`.`avatar_url` AS `author_avatar`,coalesce(`up`.`credit_score`,100) AS `author_credit_score`,`up`.`bio` AS `author_bio`,`ua`.`created_at` AS `author_join_time`,(select count(0) from `forum_post` where ((`forum_post`.`author_id` = `p`.`author_id`) and (`forum_post`.`is_deleted` = 0))) AS `author_post_count`,(select count(0) from `forum_follow_tag` where `forum_follow_tag`.`tag_id` in (select `forum_post_tag`.`tag_id` from `forum_post_tag` where (`forum_post_tag`.`post_id` = `p`.`id`))) AS `tag_follow_count` from ((`forum_post` `p` left join `user_account` `ua` on((`p`.`author_id` = `ua`.`id`))) left join `user_profile` `up` on((`p`.`author_id` = `up`.`user_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_forum_post_list`
--

/*!50001 DROP VIEW IF EXISTS `v_forum_post_list`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_forum_post_list` AS select `p`.`id` AS `id`,`p`.`title` AS `title`,`p`.`content` AS `content`,`p`.`post_type` AS `post_type`,`p`.`like_count` AS `like_count`,`p`.`comment_count` AS `comment_count`,`p`.`share_count` AS `share_count`,`p`.`collect_count` AS `collect_count`,`p`.`view_count` AS `view_count`,`p`.`published_at` AS `published_at`,`p`.`created_at` AS `created_at`,`p`.`audit_status` AS `audit_status`,`p`.`display_status` AS `display_status`,`ua`.`id` AS `author_id`,`ua`.`username` AS `author_name`,`up`.`avatar_url` AS `author_avatar`,coalesce(`up`.`credit_score`,100) AS `author_credit_score`,group_concat(distinct `t`.`tag_name` separator ',') AS `tags`,`c`.`name` AS `category_name` from (((((`forum_post` `p` left join `user_account` `ua` on((`p`.`author_id` = `ua`.`id`))) left join `user_profile` `up` on((`p`.`author_id` = `up`.`user_id`))) left join `forum_post_tag` `pt` on((`p`.`id` = `pt`.`post_id`))) left join `forum_tag` `t` on((`pt`.`tag_id` = `t`.`id`))) left join `forum_category` `c` on((`p`.`category_id` = `c`.`id`))) where (`p`.`is_deleted` = 0) group by `p`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-13 13:45:48

/*消息模块表*/
-- 1. 会话表（记录各类独立场景聊天主体）
CREATE TABLE IF NOT EXISTS `conversation` (
                                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
                                              `conversation_type` ENUM('product_consult', 'order_service', 'system') NOT NULL DEFAULT 'product_consult' COMMENT '会话类型',
    `product_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联商品ID',
    `order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联订单ID',
    `initiator_id` BIGINT UNSIGNED NOT NULL COMMENT '会话发起方用户ID',
    `receiver_id` BIGINT UNSIGNED NOT NULL COMMENT '会话接收方用户ID',
    `last_message_at` DATETIME DEFAULT NULL COMMENT '最后消息时间',
    `last_message_content` VARCHAR(500) DEFAULT NULL COMMENT '最后消息内容',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_initiator_id` (`initiator_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_last_message_at` (`last_message_at`),
    KEY `idx_conversation_type` (`conversation_type`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- 2. 聊天消息表（记录每次通讯中具体的消息内容）
CREATE TABLE IF NOT EXISTS `chat_message` (
                                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                                              `conversation_id` BIGINT UNSIGNED NOT NULL COMMENT '会话ID',
                                              `sender_id` BIGINT UNSIGNED NOT NULL COMMENT '发送者ID',
                                              `message_type` ENUM('text', 'image', 'system', 'order_card', 'product_card') NOT NULL DEFAULT 'text' COMMENT '消息类型：text-文本, image-图片, system-系统消息, order_card-订单卡片, product_card-商品卡片',
    `content` TEXT COMMENT '消息内容',
    `ext_json` JSON DEFAULT NULL COMMENT '扩展信息（如商品卡片、订单卡片的JSON数据）',
    `sent_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `recalled` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否撤回：0-未撤回, 1-已撤回',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_sent_at` (`sent_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 3. 会话已读记录表
CREATE TABLE IF NOT EXISTS `conversation_read_record` (
                                                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                                          `conversation_id` BIGINT UNSIGNED NOT NULL COMMENT '会话ID',
                                                          `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                                                          `last_read_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后阅读时间',
                                                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                          `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                          PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_user` (`conversation_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话已读记录表';

-- 4. 系统通知表（支持定向规则或全局系统通知）
CREATE TABLE IF NOT EXISTS `system_notice` (
                                               `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知ID',
                                               `notice_type` ENUM('system', 'order', 'after_sale', 'promotion', 'forum') NOT NULL DEFAULT 'system' COMMENT '通知类型：system-系统, order-订单, after_sale-售后, promotion-促销, forum-论坛',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` VARCHAR(2000) NOT NULL COMMENT '通知内容',
    `target_scope` ENUM('all', 'role', 'user') NOT NULL DEFAULT 'all' COMMENT '目标范围：all-全部, role-角色, user-指定用户',
    `target_role_code` VARCHAR(30) DEFAULT NULL COMMENT '目标角色编码（当target_scope=role时使用）',
    `target_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '目标用户ID（当target_scope=user时使用）',
    `publish_status` ENUM('draft', 'published', 'revoked') NOT NULL DEFAULT 'draft' COMMENT '发布状态：draft-草稿, published-已发布, revoked-已撤回',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建管理员ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_notice_type` (`notice_type`),
    KEY `idx_target_scope` (`target_scope`),
    KEY `idx_target_user_id` (`target_user_id`),
    KEY `idx_publish_status` (`publish_status`),
    KEY `idx_published_at` (`published_at`),
    KEY `idx_created_by` (`created_by`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';

-- 5. 个人通知收件箱表（个人通知中心信息的投递及已读）
CREATE TABLE IF NOT EXISTS `user_notice_inbox` (
                                                   `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收件箱记录ID',
                                                   `notice_id` BIGINT UNSIGNED NOT NULL COMMENT '通知ID',
                                                   `user_id` BIGINT UNSIGNED NOT NULL COMMENT '接收用户ID',
                                                   `read_status` ENUM('unread', 'read') NOT NULL DEFAULT 'unread' COMMENT '读取状态：unread-未读, read-已读',
    `delivered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
    `read_at` DATETIME DEFAULT NULL COMMENT '已读时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_read_status` (`read_status`),
    KEY `idx_delivered_at` (`delivered_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人通知收件箱表';