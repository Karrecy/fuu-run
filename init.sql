/*
 Navicat Premium Dump SQL

 Source Server         : 本地8.0
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : fuudb

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 10/04/2025 01:22:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地点',
  `detail` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址详情',
  `lon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '经度',
  `lat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '纬度',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话',
  `is_default` tinyint NOT NULL COMMENT '默认 0 否 1 是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_id` bigint NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_id` bigint NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 138 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for capital_flow
-- ----------------------------
DROP TABLE IF EXISTS `capital_flow`;
CREATE TABLE `capital_flow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单id',
  `agent_id` bigint NULL DEFAULT NULL COMMENT '代理id',
  `profit_agent` decimal(10, 2) NULL DEFAULT NULL COMMENT '代理收益',
  `runner_id` bigint NULL DEFAULT NULL COMMENT '跑腿id',
  `profit_runner` decimal(10, 2) NULL DEFAULT NULL COMMENT '跑腿收益',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `profit_user` decimal(10, 2) NULL DEFAULT NULL COMMENT '用户收益',
  `profit_plat` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台收益',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '类型 订单收益 跑腿提现 代理提现',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_agent_id`(`agent_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4059 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资金流动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of capital_flow
-- ----------------------------

-- ----------------------------
-- Table structure for money_recode
-- ----------------------------
DROP TABLE IF EXISTS `money_recode`;
CREATE TABLE `money_recode`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL COMMENT 'uid',
  `cash` decimal(10, 2) NOT NULL COMMENT '提现金额',
  `platform` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提现去向平台',
  `card` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡号',
  `status` tinyint NOT NULL COMMENT '状态 0 驳回 1 通过 2 审核中',
  `type` tinyint NOT NULL COMMENT '用户类型 ',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户备注',
  `update_id` bigint NOT NULL COMMENT '审核人',
  `update_time` datetime NOT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `feedback` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核反馈',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提现审核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of money_recode
-- ----------------------------

-- ----------------------------
-- Table structure for order_appeal
-- ----------------------------
DROP TABLE IF EXISTS `order_appeal`;
CREATE TABLE `order_appeal`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单id',
  `school_id` bigint NULL DEFAULT NULL COMMENT '学校id',
  `appeal_time` datetime NULL DEFAULT NULL COMMENT '申诉时间',
  `appeal_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申诉理由',
  `appeal_status` tinyint NULL DEFAULT NULL COMMENT '申诉状态 0 不通过 1 通过 2 申诉中',
  `update_time` datetime NULL DEFAULT NULL COMMENT '申诉更新时间',
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申诉驳回原因',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `update_type` int NULL DEFAULT NULL COMMENT '更新人类型',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_appeal
-- ----------------------------

-- ----------------------------
-- Table structure for order_attachment
-- ----------------------------
DROP TABLE IF EXISTS `order_attachment`;
CREATE TABLE `order_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单id',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件原始名',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件地址',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件后缀',
  `file_size` int NOT NULL COMMENT '文件大小字节',
  `type` int NOT NULL COMMENT '类型 1订单附件 2完成凭证 3申诉凭证',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for order_chat
-- ----------------------------
DROP TABLE IF EXISTS `order_chat`;
CREATE TABLE `order_chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送者id',
  `sender_type` int NULL DEFAULT NULL COMMENT '发送者类型',
  `recipients` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收者ids',
  `msg_type` int NULL DEFAULT NULL COMMENT '消息类型',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息体',
  `readIds` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '已读ids',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 202 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_chat
-- ----------------------------

-- ----------------------------
-- Table structure for order_main
-- ----------------------------
DROP TABLE IF EXISTS `order_main`;
CREATE TABLE `order_main`  (
  `id` bigint NOT NULL,
  `school_id` bigint NOT NULL COMMENT '学校id',
  `service_type` int NOT NULL COMMENT '服务类型 0 帮取送 1 代买 2 万能服务',
  `tag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签',
  `weight` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物品重量',
  `start_address` json NULL COMMENT '起点地址',
  `end_address` json NOT NULL COMMENT '终点地址',
  `detail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '具体描述（暴露）',
  `is_timed` tinyint NOT NULL COMMENT '是否指定时间 0 否 1 是',
  `specified_time` datetime NULL DEFAULT NULL COMMENT '指定时间',
  `auto_cancel_ttl` int NOT NULL COMMENT '未结单取消时间（秒）',
  `gender` tinyint NOT NULL COMMENT '0女 1男 2不限',
  `estimated_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '预估商品价格',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL COMMENT '订单状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `runner_id` bigint NULL DEFAULT NULL COMMENT '跑腿id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_main
-- ----------------------------

-- ----------------------------
-- Table structure for order_payment
-- ----------------------------
DROP TABLE IF EXISTS `order_payment`;
CREATE TABLE `order_payment`  (
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `additional_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '附加金额',
  `actual_payment` decimal(10, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `payment_status` tinyint NOT NULL COMMENT '支付状态 0未支付 1已支付 2退款中 3已退款',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '付款时间',
  `refund_pending_time` datetime NULL DEFAULT NULL COMMENT '退款中时间',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `is_couponed` tinyint NOT NULL COMMENT '是否使用优惠券 0 否 1 是',
  `coupon_id` bigint NULL DEFAULT NULL COMMENT '优惠券ID',
  `discount_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠金额',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_payment
-- ----------------------------

-- ----------------------------
-- Table structure for order_progress
-- ----------------------------
DROP TABLE IF EXISTS `order_progress`;
CREATE TABLE `order_progress`  (
  `order_id` bigint NOT NULL COMMENT '订单id',
  `accepted_time` datetime NULL DEFAULT NULL COMMENT '接单时间',
  `delivering_time` datetime NULL DEFAULT NULL COMMENT '开始配送时间',
  `delivered_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `completed_type` tinyint NULL DEFAULT NULL COMMENT '由谁完成',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `cancel_user_type` int NULL DEFAULT NULL COMMENT '取消人类型',
  `cancel_user_id` bigint NULL DEFAULT NULL COMMENT '取消人ID',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_progress
-- ----------------------------

-- ----------------------------
-- Table structure for oss
-- ----------------------------
DROP TABLE IF EXISTS `oss`;
CREATE TABLE `oss`  (
  `id` bigint NOT NULL COMMENT '对象存储主键',
  `file_size` int NOT NULL COMMENT '文件大小',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `type` int NOT NULL DEFAULT 0 COMMENT '文件类型',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原名',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件后缀名',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'URL地址',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '上传人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新人',
  `service` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'minio' COMMENT '服务商',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'OSS对象存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oss
-- ----------------------------
INSERT INTO `oss` VALUES (1910020328783695874, 140720, '2025/04/10/b67934218a5049ba82c1a45413be24b1png', 7, 'school', 'png', 'http://static.singoval.com/karrecy-fuu-run/2025/04/10/b67934218a5049ba82c1a45413be24b1png', '2025-04-10 01:21:47', '100', '2025-04-10 01:21:47', '100', 'aliyun');
INSERT INTO `oss` VALUES (1910020361553793026, 240374, '2025/04/10/355e59ca34ae4f32b9636ea5fed5d2capng', 7, 'school', 'png', 'http://static.singoval.com/karrecy-fuu-run/2025/04/10/355e59ca34ae4f32b9636ea5fed5d2capng', '2025-04-10 01:21:55', '100', '2025-04-10 01:21:55', '100', 'aliyun');

-- ----------------------------
-- Table structure for oss_config
-- ----------------------------
DROP TABLE IF EXISTS `oss_config`;
CREATE TABLE `oss_config`  (
  `id` bigint NOT NULL COMMENT '主建',
  `config_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '配置key',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'accessKey',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '秘钥',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '桶名称',
  `prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '前缀',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '访问站点',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '自定义域名',
  `is_https` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '域',
  `access_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否默认（0=是,1=否）',
  `ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '扩展字段',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '对象存储配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oss_config
-- ----------------------------
INSERT INTO `oss_config` VALUES (1, 'aliyun', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', '', 'XXXXXXXXXXXXXXX', '', 'N', '', '1', '1', '', '2024-12-05 06:06:42', NULL, NULL);
INSERT INTO `oss_config` VALUES (2, 'qiniu', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', '', 'XXXXXXXXXXXXXXX', '', 'N', '', '1', '0', '', '2024-12-05 06:06:42', NULL, NULL);
INSERT INTO `oss_config` VALUES (3, 'minio', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', '', 'XXXXXXXXXXXXXXX', '', 'N', '', '1', '0', '', '2024-12-05 06:06:42', NULL, NULL);
INSERT INTO `oss_config` VALUES (4, 'qcloud', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', '', 'XXXXXXXXXXXXXXX', '', 'N', 'ap-beijing', '1', '0', '', '2024-12-05 06:06:42', '2024-12-05 06:06:42', NULL);

-- ----------------------------
-- Table structure for perm
-- ----------------------------
DROP TABLE IF EXISTS `perm`;
CREATE TABLE `perm`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级id',
  `sort` int NULL DEFAULT 0 COMMENT '排序字段',
  `perms` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of perm
-- ----------------------------
INSERT INTO `perm` VALUES (1000, '地址相关', NULL, 1, NULL);
INSERT INTO `perm` VALUES (1100, '用户地址相关', 1000, 1, NULL);
INSERT INTO `perm` VALUES (1101, '新增地址', 1100, 1, 'address:address:add');
INSERT INTO `perm` VALUES (1102, '更新地址', 1100, 2, 'address:address:edit');
INSERT INTO `perm` VALUES (1103, '地址分页查询', 1100, 3, 'address:address:list');
INSERT INTO `perm` VALUES (1104, '当前用户地址列表', 1100, 4, 'address:address:list:curr');
INSERT INTO `perm` VALUES (1105, '删除地址', 1100, 5, 'address:address:delete');
INSERT INTO `perm` VALUES (1106, '用户地址菜单', 1100, 5, 'address:address:view');
INSERT INTO `perm` VALUES (1107, '根据地址ID删除当前用户的地址', 1100, 0, 'address:address:curr:delete');
INSERT INTO `perm` VALUES (1108, '根据地址ID获取当前用户的地址', 1100, 0, 'address:address:curr:get');
INSERT INTO `perm` VALUES (1200, '校区区域相关', 1000, 2, NULL);
INSERT INTO `perm` VALUES (1201, '新增校区区域', 1200, 1, 'address:region:add');
INSERT INTO `perm` VALUES (1202, '修改校区区域', 1200, 2, 'address:region:edit');
INSERT INTO `perm` VALUES (1203, '校区区域分页查询', 1200, 3, 'address:region:list');
INSERT INTO `perm` VALUES (1204, '用户校区区域分页查询', 1200, 4, 'address:region:list:user');
INSERT INTO `perm` VALUES (1205, '删除校区区域', 1200, 5, 'address:region:delete');
INSERT INTO `perm` VALUES (1206, '校区区域菜单', 1200, 5, 'address:region:view');
INSERT INTO `perm` VALUES (1300, '校区相关', 1000, 3, NULL);
INSERT INTO `perm` VALUES (1301, '新增校区', 1300, 1, 'address:school:add');
INSERT INTO `perm` VALUES (1302, '修改校区', 1300, 2, 'address:school:edit');
INSERT INTO `perm` VALUES (1303, '校区分页查询', 1300, 3, 'address:school:list');
INSERT INTO `perm` VALUES (1304, '根据id查询校区', 1300, 4, 'address:school:get');
INSERT INTO `perm` VALUES (1305, '删除校区', 1300, 5, 'address:school:delete');
INSERT INTO `perm` VALUES (1306, '校区菜单', 1300, 5, 'address:school:view');
INSERT INTO `perm` VALUES (2000, '订单相关', NULL, 4, NULL);
INSERT INTO `perm` VALUES (2100, '订单申诉', 2000, 1, 'order:appeal:view');
INSERT INTO `perm` VALUES (2101, '申诉分页查询', 2100, 1, 'order:appeal:list');
INSERT INTO `perm` VALUES (2102, '根据id查询申诉', 2100, 2, 'order:appeal:get');
INSERT INTO `perm` VALUES (2103, '提交申诉', 2100, 3, 'order:appeal:submit');
INSERT INTO `perm` VALUES (2104, '处理申诉', 2100, 4, 'order:appeal:edit');
INSERT INTO `perm` VALUES (2105, '订单申诉菜单', 2100, 5, 'order:appeal:view');
INSERT INTO `perm` VALUES (2200, '订单聊天', 2000, 5, 'order:chat:view');
INSERT INTO `perm` VALUES (2201, '订单聊天分页查询', 2200, 1, 'order:chat:list');
INSERT INTO `perm` VALUES (2202, '获取聊天初始化信息', 2200, 2, 'order:chat:initchat');
INSERT INTO `perm` VALUES (2203, '订单聊天菜单', 2200, 3, 'order:chat:view');
INSERT INTO `perm` VALUES (2300, '主订单相关', 2000, 6, NULL);
INSERT INTO `perm` VALUES (2301, '新增订单', 2300, 1, 'order:order:add');
INSERT INTO `perm` VALUES (2302, '继续支付', 2300, 2, 'order:order:payAgain');
INSERT INTO `perm` VALUES (2303, '订单退款', 2300, 3, 'order:order:refund');
INSERT INTO `perm` VALUES (2304, '取消订单', 2300, 4, 'order:order:cancel');
INSERT INTO `perm` VALUES (2305, '取消订单前置操作', 2300, 5, 'order:order:cancelbefore');
INSERT INTO `perm` VALUES (2306, '大厅订单查询', 2300, 6, 'order:order:list:hall');
INSERT INTO `perm` VALUES (2307, '用户订单查询', 2300, 7, 'order:order:list:user');
INSERT INTO `perm` VALUES (2308, '订单详情查询', 2300, 8, 'order:order:detail');
INSERT INTO `perm` VALUES (2309, '接单', 2300, 9, 'order:order:accept');
INSERT INTO `perm` VALUES (2310, '配送', 2300, 10, 'order:order:delivery');
INSERT INTO `perm` VALUES (2311, '完成订单', 2300, 11, 'order:order:complete');
INSERT INTO `perm` VALUES (2312, '补充凭证', 2300, 12, 'order:order:updateImages');
INSERT INTO `perm` VALUES (2313, '确定送达', 2300, 13, 'order:order:confirm');
INSERT INTO `perm` VALUES (2314, '获取用户/跑腿员电话', 2300, 14, 'order:order:phone');
INSERT INTO `perm` VALUES (2315, '订单菜单', 2300, 15, 'order:order:view');
INSERT INTO `perm` VALUES (2316, '订单详情页', 2300, 0, 'order:detail:view');
INSERT INTO `perm` VALUES (2400, '标签相关', 2000, 7, NULL);
INSERT INTO `perm` VALUES (2401, '新增标签', 2400, 1, 'order:tag:add');
INSERT INTO `perm` VALUES (2402, '修改标签', 2400, 2, 'order:tag:edit');
INSERT INTO `perm` VALUES (2403, '标签分页查询', 2400, 3, 'order:tag:list');
INSERT INTO `perm` VALUES (2404, '根据school和ordertype查询list', 2400, 4, 'order:tag:list:user');
INSERT INTO `perm` VALUES (2405, '删除标签', 2400, 5, 'order:tag:delete');
INSERT INTO `perm` VALUES (2406, '标签菜单', 2400, 6, 'order:tag:view');
INSERT INTO `perm` VALUES (3000, '钱相关', NULL, 8, NULL);
INSERT INTO `perm` VALUES (3100, '钱包相关', 3000, 0, NULL);
INSERT INTO `perm` VALUES (3101, '钱包分页查询', 3100, 0, 'payment:wallet:page');
INSERT INTO `perm` VALUES (3102, '钱包余额查询', 3100, 0, 'payment:wallet:curr');
INSERT INTO `perm` VALUES (3103, '钱包菜单', 3100, 0, 'payment:withdraw:view');
INSERT INTO `perm` VALUES (3200, '提现相关', 3000, 0, NULL);
INSERT INTO `perm` VALUES (3201, '提现分页查询', 3200, 0, 'payment:withdraw:page');
INSERT INTO `perm` VALUES (3202, '提交提现', 3200, 0, 'payment:recode');
INSERT INTO `perm` VALUES (3203, '处理提现', 3200, 0, 'payment:recode:edit');
INSERT INTO `perm` VALUES (3204, '查询最后一次提现', 3200, 0, 'payment:recode:last');
INSERT INTO `perm` VALUES (3205, '提现菜单', 3200, 0, 'payment:recode:view');
INSERT INTO `perm` VALUES (3206, '我的钱包菜单', 3200, 0, 'payment:mywallet:view');
INSERT INTO `perm` VALUES (3300, '资金相关', 3000, 0, NULL);
INSERT INTO `perm` VALUES (3301, '当前用户资金明细查询', 3300, 0, 'payment:flow:list');
INSERT INTO `perm` VALUES (3302, '资金菜单', 3300, 0, 'payment:capital:view');
INSERT INTO `perm` VALUES (3303, '账户明细菜单', 3300, 0, 'payment:myflow:view');
INSERT INTO `perm` VALUES (4000, '地图相关', NULL, 9, NULL);
INSERT INTO `perm` VALUES (4001, '天气查询', 4000, 1, 'amap:weather');
INSERT INTO `perm` VALUES (4002, '地图菜单', 4000, 2, 'amap:view');
INSERT INTO `perm` VALUES (5000, '登录相关', NULL, 10, NULL);
INSERT INTO `perm` VALUES (5004, '获取当前用户信息', 5000, 4, 'getInfo');
INSERT INTO `perm` VALUES (5005, '统计数据', 5000, 5, 'statistic');
INSERT INTO `perm` VALUES (6000, '系统相关', NULL, 11, NULL);
INSERT INTO `perm` VALUES (6100, '对象存储配置相关', 6000, 1, NULL);
INSERT INTO `perm` VALUES (6101, '查询对象存储配置列表', 6100, 1, 'system:oss:config:list');
INSERT INTO `perm` VALUES (6102, '查询对象存储配置', 6100, 2, 'system:oss:config:get');
INSERT INTO `perm` VALUES (6103, '新增对象存储配置', 6100, 3, 'system:oss:config:add');
INSERT INTO `perm` VALUES (6104, '修改对象存储配置', 6100, 4, 'system:oss:config:edit');
INSERT INTO `perm` VALUES (6105, '删除对象存储配置', 6100, 5, 'system:oss:config:delete');
INSERT INTO `perm` VALUES (6106, '修改对象存储配置状态', 6100, 6, 'system:oss:config:changeStatus');
INSERT INTO `perm` VALUES (6107, '对象存储配置菜单', 6100, 7, 'oss:config:view');
INSERT INTO `perm` VALUES (6200, 'oss相关', 6000, 12, NULL);
INSERT INTO `perm` VALUES (6201, '查询对象存储列表', 6200, 1, 'system:oss:list');
INSERT INTO `perm` VALUES (6202, '上传对象存储', 6200, 2, 'system:oss:upload');
INSERT INTO `perm` VALUES (6203, '删除对象存储', 6200, 3, 'system:oss:delete');
INSERT INTO `perm` VALUES (6204, '下载对象存储', 6200, 4, 'system:oss:download');
INSERT INTO `perm` VALUES (6205, 'oss菜单', 6200, 5, 'oss:oss:view');
INSERT INTO `perm` VALUES (6300, '个人信息相关', 6000, 13, NULL);
INSERT INTO `perm` VALUES (6301, '更新个人信息', 6300, 1, 'system:profile:update');
INSERT INTO `perm` VALUES (6302, '绑定手机号', 6300, 2, 'system:profile:bindPhone');
INSERT INTO `perm` VALUES (6303, '是否可以请求手机号', 6300, 3, 'system:profile:canReqPhone');
INSERT INTO `perm` VALUES (6304, '个人信息菜单', 6300, 4, 'profile:view');
INSERT INTO `perm` VALUES (6305, '发送邮箱验证码', 6300, 0, 'system:profile:sendEmailCode');
INSERT INTO `perm` VALUES (6306, '绑定邮箱', 6300, 0, 'system:profile:bindEmail');
INSERT INTO `perm` VALUES (6307, '修改密码', 6300, 0, 'system:profile:updatePwd');
INSERT INTO `perm` VALUES (6400, '跑腿申请相关', 6000, 14, NULL);
INSERT INTO `perm` VALUES (6401, '跑腿申请分页查询', 6400, 1, 'system:runnerApply:list');
INSERT INTO `perm` VALUES (6402, '提交跑腿申请', 6400, 2, 'system:runnerApply:submit');
INSERT INTO `perm` VALUES (6403, '处理跑腿申请', 6400, 3, 'system:runnerApply:edit');
INSERT INTO `perm` VALUES (6404, '查询自己的申请进度', 6400, 4, 'system:runnerApply:process');
INSERT INTO `perm` VALUES (6405, '跑腿申请菜单', 6400, 5, 'runnerApply:view');
INSERT INTO `perm` VALUES (6500, '系统配置相关', 6000, 15, NULL);
INSERT INTO `perm` VALUES (6501, '修改项目配置', 6500, 0, 'system:system:config:edit');
INSERT INTO `perm` VALUES (6502, '系统配置菜单', 6500, 2, 'system:system:config:view');
INSERT INTO `perm` VALUES (6600, '角色权限相关', 6000, 16, NULL);
INSERT INTO `perm` VALUES (6601, '查询全部权限', 6600, 1, 'system:system:perms:list');
INSERT INTO `perm` VALUES (6602, '查询权限', 6600, 2, 'system:system:perms:get');
INSERT INTO `perm` VALUES (6603, '分配权限', 6600, 0, 'system:system:roleperms:handle');
INSERT INTO `perm` VALUES (6604, '角色权限菜单', 6600, 4, 'system:system:perms:view');
INSERT INTO `perm` VALUES (6605, '添加权限', 6600, 0, 'system:system:perms:add');
INSERT INTO `perm` VALUES (6606, '删除权限', 6600, 0, 'system:system:perms:del');
INSERT INTO `perm` VALUES (6700, 'pc用户相关', 6000, 17, 'system:userpc:view');
INSERT INTO `perm` VALUES (6701, '新增pc用户', 6700, 1, 'system:userpc:add');
INSERT INTO `perm` VALUES (6702, '修改pc用户', 6700, 2, 'system:userpc:edit');
INSERT INTO `perm` VALUES (6703, 'pc用户分页查询', 6700, 3, 'system:userpc:list');
INSERT INTO `perm` VALUES (6704, '删除pc用户', 6700, 4, 'system:userpc:delete');
INSERT INTO `perm` VALUES (6705, 'pc用户菜单', 6700, 5, 'system:userpc:view');
INSERT INTO `perm` VALUES (6706, '重设密码(手机号)', 6700, 0, 'system:userpc:resetPwd');
INSERT INTO `perm` VALUES (6800, '小程序用户相关', 6000, 18, NULL);
INSERT INTO `perm` VALUES (6801, '小程序用户分页查询', 6800, 1, 'system:userwx:list');
INSERT INTO `perm` VALUES (6802, '小程序用户菜单', 6800, 2, 'system:userwx:view');
INSERT INTO `perm` VALUES (6803, '修改小程序用户', 6800, 0, 'system:userwx:edit');
INSERT INTO `perm` VALUES (6900, '系统监控相关', 6000, 0, NULL);
INSERT INTO `perm` VALUES (6901, '监控菜单', 6900, 0, 'system:system:monitor:view');
INSERT INTO `perm` VALUES (6902, '获取服务监控', 6900, 0, 'system:system:monitor:get');
INSERT INTO `perm` VALUES (7000, '数据展示相关', 0, 0, NULL);
INSERT INTO `perm` VALUES (7100, '分析页相关', 7000, 0, NULL);
INSERT INTO `perm` VALUES (7101, '分析页浏览', 7100, 0, 'dashboard:analysis:view');
INSERT INTO `perm` VALUES (7102, '分析数据获取', 7100, 0, 'statistic');
INSERT INTO `perm` VALUES (7200, '轮播图管理', 7000, 0, NULL);
INSERT INTO `perm` VALUES (7201, '轮播图管理', 7200, 0, 'system:system:carousel:view');
INSERT INTO `perm` VALUES (7202, '添加轮播图', 7200, 0, 'system:system:carousel:add');
INSERT INTO `perm` VALUES (7203, '编辑轮播图', 7200, 0, 'system:system:carousel:update');
INSERT INTO `perm` VALUES (7204, '删除轮播图', 7200, 0, 'system:system:carousel:del');
INSERT INTO `perm` VALUES (16004, '删除角色权限', 16000, 4, 'system:system:roleperms:del');

-- ----------------------------
-- Table structure for role_perm
-- ----------------------------
DROP TABLE IF EXISTS `role_perm`;
CREATE TABLE `role_perm`  (
  `role_id` bigint NOT NULL,
  `perm_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`, `perm_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和权限 关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_perm
-- ----------------------------
INSERT INTO `role_perm` VALUES (1, 1201);
INSERT INTO `role_perm` VALUES (1, 1202);
INSERT INTO `role_perm` VALUES (1, 1203);
INSERT INTO `role_perm` VALUES (1, 1205);
INSERT INTO `role_perm` VALUES (1, 1206);
INSERT INTO `role_perm` VALUES (1, 1303);
INSERT INTO `role_perm` VALUES (1, 2200);
INSERT INTO `role_perm` VALUES (1, 2201);
INSERT INTO `role_perm` VALUES (1, 2202);
INSERT INTO `role_perm` VALUES (1, 2203);
INSERT INTO `role_perm` VALUES (1, 2306);
INSERT INTO `role_perm` VALUES (1, 2308);
INSERT INTO `role_perm` VALUES (1, 2314);
INSERT INTO `role_perm` VALUES (1, 2315);
INSERT INTO `role_perm` VALUES (1, 2316);
INSERT INTO `role_perm` VALUES (1, 2400);
INSERT INTO `role_perm` VALUES (1, 2401);
INSERT INTO `role_perm` VALUES (1, 2402);
INSERT INTO `role_perm` VALUES (1, 2403);
INSERT INTO `role_perm` VALUES (1, 2404);
INSERT INTO `role_perm` VALUES (1, 2405);
INSERT INTO `role_perm` VALUES (1, 2406);
INSERT INTO `role_perm` VALUES (1, 3102);
INSERT INTO `role_perm` VALUES (1, 3202);
INSERT INTO `role_perm` VALUES (1, 3204);
INSERT INTO `role_perm` VALUES (1, 3206);
INSERT INTO `role_perm` VALUES (1, 3301);
INSERT INTO `role_perm` VALUES (1, 3303);
INSERT INTO `role_perm` VALUES (1, 4000);
INSERT INTO `role_perm` VALUES (1, 4001);
INSERT INTO `role_perm` VALUES (1, 4002);
INSERT INTO `role_perm` VALUES (1, 5004);
INSERT INTO `role_perm` VALUES (1, 6202);
INSERT INTO `role_perm` VALUES (1, 6300);
INSERT INTO `role_perm` VALUES (1, 6301);
INSERT INTO `role_perm` VALUES (1, 6302);
INSERT INTO `role_perm` VALUES (1, 6303);
INSERT INTO `role_perm` VALUES (1, 6304);
INSERT INTO `role_perm` VALUES (1, 6305);
INSERT INTO `role_perm` VALUES (1, 6306);
INSERT INTO `role_perm` VALUES (1, 6307);
INSERT INTO `role_perm` VALUES (1, 6400);
INSERT INTO `role_perm` VALUES (1, 6401);
INSERT INTO `role_perm` VALUES (1, 6402);
INSERT INTO `role_perm` VALUES (1, 6403);
INSERT INTO `role_perm` VALUES (1, 6404);
INSERT INTO `role_perm` VALUES (1, 6405);
INSERT INTO `role_perm` VALUES (1, 6800);
INSERT INTO `role_perm` VALUES (1, 6801);
INSERT INTO `role_perm` VALUES (1, 6802);
INSERT INTO `role_perm` VALUES (2, 1000);
INSERT INTO `role_perm` VALUES (2, 1100);
INSERT INTO `role_perm` VALUES (2, 1101);
INSERT INTO `role_perm` VALUES (2, 1102);
INSERT INTO `role_perm` VALUES (2, 1103);
INSERT INTO `role_perm` VALUES (2, 1104);
INSERT INTO `role_perm` VALUES (2, 1105);
INSERT INTO `role_perm` VALUES (2, 1106);
INSERT INTO `role_perm` VALUES (2, 1107);
INSERT INTO `role_perm` VALUES (2, 1108);
INSERT INTO `role_perm` VALUES (2, 1200);
INSERT INTO `role_perm` VALUES (2, 1201);
INSERT INTO `role_perm` VALUES (2, 1202);
INSERT INTO `role_perm` VALUES (2, 1203);
INSERT INTO `role_perm` VALUES (2, 1204);
INSERT INTO `role_perm` VALUES (2, 1205);
INSERT INTO `role_perm` VALUES (2, 1206);
INSERT INTO `role_perm` VALUES (2, 1300);
INSERT INTO `role_perm` VALUES (2, 1301);
INSERT INTO `role_perm` VALUES (2, 1302);
INSERT INTO `role_perm` VALUES (2, 1303);
INSERT INTO `role_perm` VALUES (2, 1304);
INSERT INTO `role_perm` VALUES (2, 1305);
INSERT INTO `role_perm` VALUES (2, 1306);
INSERT INTO `role_perm` VALUES (2, 2000);
INSERT INTO `role_perm` VALUES (2, 2100);
INSERT INTO `role_perm` VALUES (2, 2101);
INSERT INTO `role_perm` VALUES (2, 2102);
INSERT INTO `role_perm` VALUES (2, 2103);
INSERT INTO `role_perm` VALUES (2, 2104);
INSERT INTO `role_perm` VALUES (2, 2105);
INSERT INTO `role_perm` VALUES (2, 2200);
INSERT INTO `role_perm` VALUES (2, 2201);
INSERT INTO `role_perm` VALUES (2, 2202);
INSERT INTO `role_perm` VALUES (2, 2203);
INSERT INTO `role_perm` VALUES (2, 2300);
INSERT INTO `role_perm` VALUES (2, 2301);
INSERT INTO `role_perm` VALUES (2, 2302);
INSERT INTO `role_perm` VALUES (2, 2303);
INSERT INTO `role_perm` VALUES (2, 2304);
INSERT INTO `role_perm` VALUES (2, 2305);
INSERT INTO `role_perm` VALUES (2, 2306);
INSERT INTO `role_perm` VALUES (2, 2307);
INSERT INTO `role_perm` VALUES (2, 2308);
INSERT INTO `role_perm` VALUES (2, 2309);
INSERT INTO `role_perm` VALUES (2, 2310);
INSERT INTO `role_perm` VALUES (2, 2311);
INSERT INTO `role_perm` VALUES (2, 2312);
INSERT INTO `role_perm` VALUES (2, 2313);
INSERT INTO `role_perm` VALUES (2, 2314);
INSERT INTO `role_perm` VALUES (2, 2315);
INSERT INTO `role_perm` VALUES (2, 2316);
INSERT INTO `role_perm` VALUES (2, 2400);
INSERT INTO `role_perm` VALUES (2, 2401);
INSERT INTO `role_perm` VALUES (2, 2402);
INSERT INTO `role_perm` VALUES (2, 2403);
INSERT INTO `role_perm` VALUES (2, 2404);
INSERT INTO `role_perm` VALUES (2, 2405);
INSERT INTO `role_perm` VALUES (2, 2406);
INSERT INTO `role_perm` VALUES (2, 3000);
INSERT INTO `role_perm` VALUES (2, 3100);
INSERT INTO `role_perm` VALUES (2, 3101);
INSERT INTO `role_perm` VALUES (2, 3102);
INSERT INTO `role_perm` VALUES (2, 3103);
INSERT INTO `role_perm` VALUES (2, 3200);
INSERT INTO `role_perm` VALUES (2, 3201);
INSERT INTO `role_perm` VALUES (2, 3202);
INSERT INTO `role_perm` VALUES (2, 3203);
INSERT INTO `role_perm` VALUES (2, 3204);
INSERT INTO `role_perm` VALUES (2, 3205);
INSERT INTO `role_perm` VALUES (2, 3206);
INSERT INTO `role_perm` VALUES (2, 3300);
INSERT INTO `role_perm` VALUES (2, 3301);
INSERT INTO `role_perm` VALUES (2, 3302);
INSERT INTO `role_perm` VALUES (2, 3303);
INSERT INTO `role_perm` VALUES (2, 4000);
INSERT INTO `role_perm` VALUES (2, 4001);
INSERT INTO `role_perm` VALUES (2, 4002);
INSERT INTO `role_perm` VALUES (2, 5004);
INSERT INTO `role_perm` VALUES (2, 5005);
INSERT INTO `role_perm` VALUES (2, 6000);
INSERT INTO `role_perm` VALUES (2, 6100);
INSERT INTO `role_perm` VALUES (2, 6101);
INSERT INTO `role_perm` VALUES (2, 6102);
INSERT INTO `role_perm` VALUES (2, 6103);
INSERT INTO `role_perm` VALUES (2, 6104);
INSERT INTO `role_perm` VALUES (2, 6105);
INSERT INTO `role_perm` VALUES (2, 6106);
INSERT INTO `role_perm` VALUES (2, 6107);
INSERT INTO `role_perm` VALUES (2, 6200);
INSERT INTO `role_perm` VALUES (2, 6201);
INSERT INTO `role_perm` VALUES (2, 6202);
INSERT INTO `role_perm` VALUES (2, 6203);
INSERT INTO `role_perm` VALUES (2, 6204);
INSERT INTO `role_perm` VALUES (2, 6205);
INSERT INTO `role_perm` VALUES (2, 6300);
INSERT INTO `role_perm` VALUES (2, 6301);
INSERT INTO `role_perm` VALUES (2, 6302);
INSERT INTO `role_perm` VALUES (2, 6303);
INSERT INTO `role_perm` VALUES (2, 6304);
INSERT INTO `role_perm` VALUES (2, 6305);
INSERT INTO `role_perm` VALUES (2, 6306);
INSERT INTO `role_perm` VALUES (2, 6307);
INSERT INTO `role_perm` VALUES (2, 6400);
INSERT INTO `role_perm` VALUES (2, 6401);
INSERT INTO `role_perm` VALUES (2, 6402);
INSERT INTO `role_perm` VALUES (2, 6403);
INSERT INTO `role_perm` VALUES (2, 6404);
INSERT INTO `role_perm` VALUES (2, 6405);
INSERT INTO `role_perm` VALUES (2, 6500);
INSERT INTO `role_perm` VALUES (2, 6501);
INSERT INTO `role_perm` VALUES (2, 6502);
INSERT INTO `role_perm` VALUES (2, 6600);
INSERT INTO `role_perm` VALUES (2, 6601);
INSERT INTO `role_perm` VALUES (2, 6602);
INSERT INTO `role_perm` VALUES (2, 6603);
INSERT INTO `role_perm` VALUES (2, 6604);
INSERT INTO `role_perm` VALUES (2, 6605);
INSERT INTO `role_perm` VALUES (2, 6606);
INSERT INTO `role_perm` VALUES (2, 6700);
INSERT INTO `role_perm` VALUES (2, 6701);
INSERT INTO `role_perm` VALUES (2, 6702);
INSERT INTO `role_perm` VALUES (2, 6703);
INSERT INTO `role_perm` VALUES (2, 6704);
INSERT INTO `role_perm` VALUES (2, 6705);
INSERT INTO `role_perm` VALUES (2, 6706);
INSERT INTO `role_perm` VALUES (2, 6800);
INSERT INTO `role_perm` VALUES (2, 6801);
INSERT INTO `role_perm` VALUES (2, 6802);
INSERT INTO `role_perm` VALUES (2, 6900);
INSERT INTO `role_perm` VALUES (2, 6901);
INSERT INTO `role_perm` VALUES (2, 6902);
INSERT INTO `role_perm` VALUES (2, 7000);
INSERT INTO `role_perm` VALUES (2, 7100);
INSERT INTO `role_perm` VALUES (2, 7101);
INSERT INTO `role_perm` VALUES (2, 7102);
INSERT INTO `role_perm` VALUES (3, 1101);
INSERT INTO `role_perm` VALUES (3, 1102);
INSERT INTO `role_perm` VALUES (3, 1104);
INSERT INTO `role_perm` VALUES (3, 1105);
INSERT INTO `role_perm` VALUES (3, 1106);
INSERT INTO `role_perm` VALUES (3, 1107);
INSERT INTO `role_perm` VALUES (3, 1108);
INSERT INTO `role_perm` VALUES (3, 1204);
INSERT INTO `role_perm` VALUES (3, 1303);
INSERT INTO `role_perm` VALUES (3, 1304);
INSERT INTO `role_perm` VALUES (3, 2101);
INSERT INTO `role_perm` VALUES (3, 2102);
INSERT INTO `role_perm` VALUES (3, 2103);
INSERT INTO `role_perm` VALUES (3, 2200);
INSERT INTO `role_perm` VALUES (3, 2201);
INSERT INTO `role_perm` VALUES (3, 2202);
INSERT INTO `role_perm` VALUES (3, 2203);
INSERT INTO `role_perm` VALUES (3, 2301);
INSERT INTO `role_perm` VALUES (3, 2302);
INSERT INTO `role_perm` VALUES (3, 2303);
INSERT INTO `role_perm` VALUES (3, 2304);
INSERT INTO `role_perm` VALUES (3, 2305);
INSERT INTO `role_perm` VALUES (3, 2306);
INSERT INTO `role_perm` VALUES (3, 2307);
INSERT INTO `role_perm` VALUES (3, 2308);
INSERT INTO `role_perm` VALUES (3, 2313);
INSERT INTO `role_perm` VALUES (3, 2314);
INSERT INTO `role_perm` VALUES (3, 2315);
INSERT INTO `role_perm` VALUES (3, 2316);
INSERT INTO `role_perm` VALUES (3, 2404);
INSERT INTO `role_perm` VALUES (3, 3102);
INSERT INTO `role_perm` VALUES (3, 3202);
INSERT INTO `role_perm` VALUES (3, 3204);
INSERT INTO `role_perm` VALUES (3, 3301);
INSERT INTO `role_perm` VALUES (3, 4000);
INSERT INTO `role_perm` VALUES (3, 4001);
INSERT INTO `role_perm` VALUES (3, 4002);
INSERT INTO `role_perm` VALUES (3, 5004);
INSERT INTO `role_perm` VALUES (3, 6202);
INSERT INTO `role_perm` VALUES (3, 6301);
INSERT INTO `role_perm` VALUES (3, 6302);
INSERT INTO `role_perm` VALUES (3, 6303);
INSERT INTO `role_perm` VALUES (3, 6304);
INSERT INTO `role_perm` VALUES (3, 6402);
INSERT INTO `role_perm` VALUES (3, 6404);
INSERT INTO `role_perm` VALUES (4, 1100);
INSERT INTO `role_perm` VALUES (4, 1101);
INSERT INTO `role_perm` VALUES (4, 1102);
INSERT INTO `role_perm` VALUES (4, 1103);
INSERT INTO `role_perm` VALUES (4, 1104);
INSERT INTO `role_perm` VALUES (4, 1105);
INSERT INTO `role_perm` VALUES (4, 1106);
INSERT INTO `role_perm` VALUES (4, 1107);
INSERT INTO `role_perm` VALUES (4, 1108);
INSERT INTO `role_perm` VALUES (4, 1203);
INSERT INTO `role_perm` VALUES (4, 1204);
INSERT INTO `role_perm` VALUES (4, 1303);
INSERT INTO `role_perm` VALUES (4, 1304);
INSERT INTO `role_perm` VALUES (4, 2101);
INSERT INTO `role_perm` VALUES (4, 2102);
INSERT INTO `role_perm` VALUES (4, 2103);
INSERT INTO `role_perm` VALUES (4, 2200);
INSERT INTO `role_perm` VALUES (4, 2201);
INSERT INTO `role_perm` VALUES (4, 2202);
INSERT INTO `role_perm` VALUES (4, 2203);
INSERT INTO `role_perm` VALUES (4, 2301);
INSERT INTO `role_perm` VALUES (4, 2302);
INSERT INTO `role_perm` VALUES (4, 2304);
INSERT INTO `role_perm` VALUES (4, 2305);
INSERT INTO `role_perm` VALUES (4, 2306);
INSERT INTO `role_perm` VALUES (4, 2307);
INSERT INTO `role_perm` VALUES (4, 2308);
INSERT INTO `role_perm` VALUES (4, 2309);
INSERT INTO `role_perm` VALUES (4, 2310);
INSERT INTO `role_perm` VALUES (4, 2311);
INSERT INTO `role_perm` VALUES (4, 2312);
INSERT INTO `role_perm` VALUES (4, 2313);
INSERT INTO `role_perm` VALUES (4, 2314);
INSERT INTO `role_perm` VALUES (4, 2315);
INSERT INTO `role_perm` VALUES (4, 2316);
INSERT INTO `role_perm` VALUES (4, 2404);
INSERT INTO `role_perm` VALUES (4, 2406);
INSERT INTO `role_perm` VALUES (4, 3102);
INSERT INTO `role_perm` VALUES (4, 3202);
INSERT INTO `role_perm` VALUES (4, 3204);
INSERT INTO `role_perm` VALUES (4, 3301);
INSERT INTO `role_perm` VALUES (4, 4000);
INSERT INTO `role_perm` VALUES (4, 4001);
INSERT INTO `role_perm` VALUES (4, 4002);
INSERT INTO `role_perm` VALUES (4, 5004);
INSERT INTO `role_perm` VALUES (4, 6202);
INSERT INTO `role_perm` VALUES (4, 6301);
INSERT INTO `role_perm` VALUES (4, 6302);
INSERT INTO `role_perm` VALUES (4, 6303);
INSERT INTO `role_perm` VALUES (4, 6304);
INSERT INTO `role_perm` VALUES (4, 6404);

-- ----------------------------
-- Table structure for runner_apply
-- ----------------------------
DROP TABLE IF EXISTS `runner_apply`;
CREATE TABLE `runner_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `school_id` bigint NOT NULL COMMENT '学校id',
  `school_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称',
  `realname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` tinyint NOT NULL COMMENT '性别 0 女 1 男',
  `student_card_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生证',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` tinyint NOT NULL COMMENT '申请状态 0驳回 1 通过 2申请中',
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_id` bigint NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '跑腿申请表\r\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of runner_apply
-- ----------------------------

-- ----------------------------
-- Table structure for school
-- ----------------------------
DROP TABLE IF EXISTS `school`;
CREATE TABLE `school`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `belong_uid` bigint NOT NULL COMMENT '属于谁管理',
  `adcode` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市编码表',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校logo',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` tinyint NOT NULL COMMENT '状态 0 禁用 1 启用',
  `profit_plat` tinyint NOT NULL COMMENT '平台收益占比',
  `profit_agent` tinyint NOT NULL COMMENT '代理收益占比',
  `profit_runner` tinyint NOT NULL COMMENT '跑腿收益占比',
  `floor_price` decimal(10, 2) NOT NULL COMMENT '底价',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uqe_name`(`name` ASC) USING BTREE,
  INDEX `idx_belong_uid`(`belong_uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of school
-- ----------------------------
INSERT INTO `school` VALUES (1, 100, '522728', '福州大学(旗山校区)', '', '2025-02-15 12:30:36', '2025-02-15 12:30:36', 1, 10, 10, 80, 0.02);

-- ----------------------------
-- Table structure for school_region
-- ----------------------------
DROP TABLE IF EXISTS `school_region`;
CREATE TABLE `school_region`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `school_id` bigint NOT NULL,
  `type` tinyint NOT NULL COMMENT '0 区域 1 楼栋',
  `name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `lon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经度',
  `lat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '纬度',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '区域id',
  `remark` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_id` bigint NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `update_id` bigint NOT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 152 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学校楼栋管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of school_region
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_daily
-- ----------------------------
DROP TABLE IF EXISTS `statistics_daily`;
CREATE TABLE `statistics_daily`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `total_orders` int NOT NULL DEFAULT 0 COMMENT '总订单量',
  `canceled_orders` int NOT NULL DEFAULT 0 COMMENT '取消订单量',
  `appealed_orders` int NOT NULL DEFAULT 0 COMMENT '申诉订单量',
  `completed_orders` int NOT NULL DEFAULT 0 COMMENT '完成订单量',
  `completion_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '完单率(%)',
  `delivery_orders` int NOT NULL DEFAULT 0 COMMENT '帮取送订单量',
  `purchase_orders` int NOT NULL DEFAULT 0 COMMENT '代买订单量',
  `universal_orders` int NOT NULL DEFAULT 0 COMMENT '万能服务订单量',
  `delivery_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '帮取送订单占比(%)',
  `purchase_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '代买订单占比(%)',
  `universal_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '万能订单占比(%)',
  `total_payment` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '总收款金额',
  `total_refund` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '总退款金额',
  `platform_profit` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '平台总收益',
  `agent_profit` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '代理总收益',
  `runner_profit` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '跑腿总收益',
  `total_visits` int NOT NULL DEFAULT 0 COMMENT '总访问量',
  `unique_visitors` int NOT NULL DEFAULT 0 COMMENT '独立访问用户数',
  `malicious_requests` int NOT NULL DEFAULT 0 COMMENT '恶意请求数量',
  `new_users` int NOT NULL DEFAULT 0 COMMENT '新增用户数',
  `active_users` int NOT NULL DEFAULT 0 COMMENT '活跃用户数',
  `new_runners` int NOT NULL DEFAULT 0 COMMENT '新增跑腿用户数',
  `active_runners` int NOT NULL DEFAULT 0 COMMENT '活跃跑腿用户数',
  `create_time` date NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日数据统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of statistics_daily
-- ----------------------------

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `school_id` bigint NOT NULL,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'tag',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `service_type` int NOT NULL COMMENT '服务类型 0 帮取送 1 代买 2 万能服务',
  `create_time` datetime NOT NULL,
  `create_id` bigint NOT NULL,
  `update_time` datetime NOT NULL,
  `update_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'tag表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tags
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` bigint NOT NULL AUTO_INCREMENT COMMENT '全局uid',
  `device_type` tinyint NOT NULL COMMENT '0 pc 1 小程序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `login_time` datetime NOT NULL COMMENT '上次登录时间',
  `login_ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录ip',
  `login_region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录地址',
  `user_type` int NOT NULL COMMENT '用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户',
  `create_id` bigint NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_id` bigint NOT NULL COMMENT '更新人',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '全局用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (100, 0, '2024-11-30 08:28:07', '2025-04-10 01:17:50', '127.0.0.1', '内网IP|内网IP', 0, 100, '2025-03-21 19:19:46', 100);

-- ----------------------------
-- Table structure for user_pc
-- ----------------------------
DROP TABLE IF EXISTS `user_pc`;
CREATE TABLE `user_pc`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `username` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `student_card_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生证',
  `id_card_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证',
  `sex` tinyint NOT NULL COMMENT '0 女 1 男',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0 禁用 1 启用',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `email_enable` tinyint NULL DEFAULT NULL COMMENT '是否启用邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_pc
-- ----------------------------
INSERT INTO `user_pc` VALUES (100, 100, 'admin', '$2a$10$ol7ZknlMmL7.FK7TEb6VH.QlA7193O0KyTYGEFC0t1v7GaghAsHaC', '15612345678', '阿三', 'url', 'url', 1, 1, '', 'test@qq.com', 1);

-- ----------------------------
-- Table structure for user_wx
-- ----------------------------
DROP TABLE IF EXISTS `user_wx`;
CREATE TABLE `user_wx`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '小程序唯一id',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机',
  `points` int NOT NULL DEFAULT 0 COMMENT '积分',
  `is_runner` tinyint NOT NULL DEFAULT 0 COMMENT '是否跑腿 0 否 1 是',
  `can_order` tinyint NOT NULL DEFAULT 0 COMMENT '是否可以下单 0 否 1 是',
  `can_take` tinyint NOT NULL DEFAULT 0 COMMENT '是否可以接单 0 否 1 是',
  `school_id` bigint NULL DEFAULT NULL COMMENT '跑腿绑定学校id',
  `realname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跑腿真实姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '跑腿性别',
  `credit_score` int NULL DEFAULT NULL COMMENT '信用分',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_uid`(`uid` ASC) USING BTREE,
  UNIQUE INDEX `idx_openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '小程序普通用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_wx
-- ----------------------------

-- ----------------------------
-- Table structure for wallet
-- ----------------------------
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet`  (
  `uid` bigint NOT NULL COMMENT 'uid',
  `withdrawn` decimal(10, 2) NOT NULL COMMENT '当前余额',
  `balance` decimal(10, 2) NOT NULL COMMENT '已提现',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户账户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wallet
-- ----------------------------
INSERT INTO `wallet` VALUES (100, 0.00, 0.00, '2025-02-18 11:43:19', '2025-03-21 22:46:17');

SET FOREIGN_KEY_CHECKS = 1;
