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
INSERT INTO `category` VALUES (910001,'联调-手机数码',1,1,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(910002,'联调-电脑办公',2,1,'2026-04-12 22:26:20','2026-04-12 22:26:20');
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
INSERT INTO `order_item` VALUES (950001,940001,920001,'联调测试 iPhone 13 128G','/uploads/products/920001_cover.jpg',2999.00,1,2999.00,'2026-04-12 10:00:00'),(950002,940002,920002,'联调测试 ThinkPad X1','/uploads/products/920002_cover.jpg',3499.00,1,3499.00,'2026-04-12 10:05:00'),(950003,940003,920001,'联调测试 iPhone 13 128G','/uploads/products/920001_cover.jpg',2999.00,1,2999.00,'2026-04-12 11:30:00'),(950004,940004,920001,'联调测试 iPhone 13 128G','/uploads/products/920001_cover.jpg',2999.00,1,2999.00,'2026-04-12 22:27:49'),(950005,940005,920003,'联调测试 AirPods Pro','/uploads/products/920003_cover.jpg',899.00,2,1798.00,'2026-04-12 22:37:57'),(950006,940006,920003,'联调测试 AirPods Pro','/uploads/products/920003_cover.jpg',899.00,2,1798.00,'2026-04-13 00:25:42'),(950007,940007,920003,'联调测试 AirPods Pro','/uploads/products/920003_cover.jpg',899.00,2,1798.00,'2026-04-13 00:25:52'),(950008,940008,920003,'联调测试 AirPods Pro','/uploads/products/920003_cover.jpg',899.00,2,1798.00,'2026-04-13 11:54:33'),(950009,940009,920003,'联调测试 AirPods Pro','/uploads/products/920003_cover.jpg',899.00,2,1798.00,'2026-04-13 12:36:57');
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
INSERT INTO `product` VALUES (920001,900001,910001,'联调测试 iPhone 13 128G','接口联调用样品','用于订单接口创建/列表/详情测试','Apple','iPhone 13','almost_new',2023,5199.00,2999.00,1,'both','上海','徐家汇地铁站2号口',NULL,NULL,19,'on_sale',0,0,'2026-04-12 09:00:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-12 22:27:49'),(920002,900001,910002,'联调测试 ThinkPad X1','接口联调用样品','用于订单接口创建/取消测试','Lenovo','X1 Carbon','good',2022,7999.00,3499.00,1,'shipping','上海','漕河泾',NULL,NULL,10,'on_sale',0,0,'2026-04-12 09:10:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(920003,900002,910001,'联调测试 AirPods Pro','接口联调用样品','用于跨卖家场景测试','Apple','AirPods Pro','good',2023,1999.00,899.00,1,'both','北京','望京SOHO',NULL,NULL,11,'on_sale',0,0,'2026-04-12 09:20:00',NULL,NULL,'2026-04-12 22:26:20','2026-04-13 12:36:57');
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
INSERT INTO `product_image` VALUES (930001,920001,'/uploads/products/920001_cover.jpg',1,1,'2026-04-12 22:26:20'),(930002,920002,'/uploads/products/920002_cover.jpg',1,1,'2026-04-12 22:26:20'),(930003,920003,'/uploads/products/920003_cover.jpg',1,1,'2026-04-12 22:26:20');
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
INSERT INTO `trade_order` VALUES (940001,'ODSEED202604120001',10001,900001,'pending_payment','shipping',2999.00,12.00,3011.00,'联调-待支付订单','张三','13800000001','上海市徐汇区测试路1号',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-12 10:00:00','2026-04-12 10:00:00'),(940002,'ODSEED202604120002',10001,900001,'shipped','shipping',3499.00,0.00,3499.00,'联调-已发货订单','张三','13800000001','上海市徐汇区测试路2号',NULL,NULL,'2026-04-12 10:10:00','2026-04-12 11:00:00',NULL,NULL,NULL,'2026-04-12 10:05:00','2026-04-12 11:00:00'),(940003,'ODSEED202604120003',10001,900001,'cancelled','pickup',2999.00,0.00,2999.00,'联调-已取消订单',NULL,NULL,NULL,'徐家汇地铁站2号口','买家临时取消',NULL,NULL,NULL,NULL,'2026-04-12 12:00:00','2026-04-12 11:30:00','2026-04-12 12:00:00'),(940004,'OD202604122227494620',10009,900001,'pending_payment','shipping',2999.00,12.00,3011.00,'尽快发货','张三','13800000001','上海市徐汇区漕溪北路100号',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-12 22:27:49','2026-04-12 22:27:49'),(940005,'OD202604122237579036',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','李四','13800000023','广东省深圳市南山区粤海大学',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-12 22:39:23','2026-04-12 22:37:57','2026-04-12 22:37:57'),(940006,'OD202604130025426622',10009,900002,'completed','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,NULL,'2026-04-13 12:05:37','2026-04-13 12:27:05','2026-04-13 12:33:22','2026-04-13 13:03:18',NULL,'2026-04-13 00:25:42','2026-04-13 00:25:42'),(940007,'OD202604130025525935',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-13 00:26:38','2026-04-13 00:25:52','2026-04-13 00:25:52'),(940008,'OD202604131154339875',10009,900002,'cancelled','shipping',1798.00,15.00,1813.00,'尽快发货','张三','13800000001','上海市徐汇区虹桥路1号',NULL,'买家临时不需要了',NULL,NULL,NULL,NULL,'2026-04-13 11:56:04','2026-04-13 11:54:33','2026-04-13 11:54:33'),(940009,'OD202604131236575883',10009,900002,'delivered','pickup',1798.00,15.00,1813.00,'尽快发货',NULL,NULL,NULL,'深圳大学沧海致腾楼240',NULL,'2026-04-13 12:38:37','2026-04-13 12:40:53','2026-04-13 13:01:30',NULL,NULL,'2026-04-13 12:36:57','2026-04-13 12:36:57');
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
INSERT INTO `user_account` VALUES (1,'admin','admin','13800000000','admin@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,1,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10001,'张三','张三','13800000001','zhangsan@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,'李四','李四','13800000002','lisi@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,'王五','王五','13800000003','wangwu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,'赵六','赵六','13800000004','zhaoliu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,'小明','小明','13800000005','xiaoming@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,'小红','小红','13800000006','xiaohong@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'banned',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,'大刘','大刘','13800000007','daliu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,'小陈','小陈','13800000008','xiaochen@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:18:33','2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,'apitest_fixed','接口联调账号','13990009999','apitest_fixed@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,0,'pending','2026-04-13 13:27:07','2026-04-12 22:23:21','2026-04-12 22:23:21','2026-04-13 13:27:07'),(900001,'seed_seller_1','测试卖家1','13990000001','seed_seller_1@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',1,1,0,'active',NULL,'2026-04-12 22:26:20','2026-04-12 22:26:20','2026-04-12 22:26:20'),(900002,'seed_seller_2','测试卖家2','13990000002','seed_seller_2@example.com','$2a$10$OvRd93smfGtXikKcK7Abu.HIjikAl17pLIFYJbOM7Nyp7Ew6zrni.',1,1,0,'active','2026-04-13 13:25:53','2026-04-12 22:26:20','2026-04-12 22:26:20','2026-04-13 13:25:53'),(900003,'admin_test','管理员测试账号','13900000099','admin_test@example.com','$2a$10$kiQWh1xhPS1rO57D.UwBA.xjsvEJLM86buu0FQSyPWw5Z2CyUbGoK',1,1,1,'active','2026-04-13 13:40:39','2026-04-13 00:44:30','2026-04-13 00:44:30','2026-04-13 13:40:39');
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
INSERT INTO `user_profile` VALUES (10001,'/avatars/zhangsan.jpg','unknown',NULL,'Digital goods enthusiast, trusted trader',NULL,NULL,95,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10002,'/avatars/lisi.jpg','unknown',NULL,'Student buyer looking for used textbooks',NULL,NULL,88,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10003,'/avatars/wangwu.jpg','unknown',NULL,'Professional seller with fair prices',NULL,NULL,92,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10004,'/avatars/zhaoliu.jpg','unknown',NULL,'New to the platform, nice to meet you',NULL,NULL,75,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10005,'/avatars/xiaoming.jpg','unknown',NULL,'Enjoys hunting for second-hand deals',NULL,NULL,85,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10006,'/avatars/xiaohong.jpg','unknown',NULL,'Focuses on sharing mother-and-baby products',NULL,NULL,60,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10007,'/avatars/daliu.jpg','unknown',NULL,'Photography lover and gear collector',NULL,NULL,98,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10008,'/avatars/xiaochen.jpg','unknown',NULL,'Bargain hunter',NULL,NULL,82,100.00,0,'2026-04-12 22:18:33','2026-04-12 22:18:33'),(10009,NULL,'unknown',NULL,NULL,NULL,NULL,100,100.00,0,'2026-04-12 22:23:21','2026-04-12 22:23:21'),(900001,'/avatars/seed_seller_1.jpg','unknown',NULL,'订单联调测试卖家1',NULL,NULL,96,100.00,0,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(900002,'/avatars/seed_seller_2.jpg','unknown',NULL,'订单联调测试卖家2',NULL,NULL,94,100.00,0,'2026-04-12 22:26:20','2026-04-12 22:26:20'),(900003,NULL,'unknown',NULL,NULL,NULL,NULL,100,100.00,0,'2026-04-13 00:44:30','2026-04-13 00:44:30');
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
