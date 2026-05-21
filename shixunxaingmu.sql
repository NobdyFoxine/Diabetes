/*
 Navicat Premium Dump SQL

 Source Server         : mac
 Source Server Type    : MySQL
 Source Server Version : 90500 (9.5.0)
 Source Host           : localhost:3306
 Source Schema         : shixunxaingmu

 Target Server Type    : MySQL
 Target Server Version : 90500 (9.5.0)
 File Encoding         : 65001

 Date: 21/05/2026 14:36:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_alert_threshold
-- ----------------------------
DROP TABLE IF EXISTS `biz_alert_threshold`;
CREATE TABLE `biz_alert_threshold` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL COMMENT '关联 sys_user.id（患者）',
  `glucose_low` decimal(4,1) DEFAULT '3.9' COMMENT '血糖过低临界值 (mmol/L)',
  `glucose_warn_low` decimal(4,1) DEFAULT '4.5' COMMENT '血糖偏低关注值',
  `glucose_warn_high` decimal(4,1) DEFAULT '10.0' COMMENT '血糖偏高关注值',
  `glucose_high` decimal(4,1) DEFAULT '16.7' COMMENT '血糖过高临界值',
  `systolic_max` int DEFAULT '140' COMMENT '收缩压最高 (mmHg)',
  `diastolic_max` int DEFAULT '90' COMMENT '舒张压最高 (mmHg)',
  `systolic_warn` int DEFAULT '130' COMMENT '收缩压关注值',
  `diastolic_warn` int DEFAULT '85' COMMENT '舒张压关注值',
  `notes` varchar(200) DEFAULT NULL COMMENT '备注（如"高龄患者降糖方案"）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_patient_id` (`patient_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='患者个性化预警阈值表';

-- ----------------------------
-- Records of biz_alert_threshold
-- ----------------------------
BEGIN;
INSERT INTO `biz_alert_threshold` (`id`, `patient_id`, `glucose_low`, `glucose_warn_low`, `glucose_warn_high`, `glucose_high`, `systolic_max`, `diastolic_max`, `systolic_warn`, `diastolic_warn`, `notes`, `create_time`, `update_time`) VALUES (1, 10001, 3.9, 4.5, 10.0, 16.7, 140, 90, 130, 85, '', '2026-05-19 09:53:26', '2026-05-19 09:53:26');
COMMIT;

-- ----------------------------
-- Table structure for biz_followup_plan
-- ----------------------------
DROP TABLE IF EXISTS `biz_followup_plan`;
CREATE TABLE `biz_followup_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `plan_date` date NOT NULL COMMENT '计划执行日期',
  `content` varchar(255) NOT NULL COMMENT '随访内容',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0-待执行, 1-已完成, 2-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='随访计划表';

-- ----------------------------
-- Records of biz_followup_plan
-- ----------------------------
BEGIN;
INSERT INTO `biz_followup_plan` (`id`, `patient_id`, `doctor_id`, `plan_date`, `content`, `status`, `create_time`) VALUES (2, 10001, 20001, '2026-05-26', '请记录本周内空腹及餐后血糖波动情况\n用药依从性自评\n足部检查（观察有无破损、感染）', 1, '2026-05-19 09:51:55');
INSERT INTO `biz_followup_plan` (`id`, `patient_id`, `doctor_id`, `plan_date`, `content`, `status`, `create_time`) VALUES (3, 10001, 20001, '2026-05-19', '【高血糖应急处理指南】\n当血糖超过16.7 mmol/L时，需立即就医。本文介绍高血糖的识别、应急处理步骤和预防措施。\n\n—— 来自你的主管医生', 0, '2026-05-19 10:26:57');
INSERT INTO `biz_followup_plan` (`id`, `patient_id`, `doctor_id`, `plan_date`, `content`, `status`, `create_time`) VALUES (4, 40002, 20001, '2026-05-21', '【低血糖预防与饮食调整】\n调整进食时间和食物结构，避免因饮食不当导致的低血糖事件。\n\n—— 来自你的主管医生', 0, '2026-05-21 14:32:17');
COMMIT;

-- ----------------------------
-- Table structure for biz_followup_template
-- ----------------------------
DROP TABLE IF EXISTS `biz_followup_template`;
CREATE TABLE `biz_followup_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '模板名称（强化管理/常规管理）',
  `description` varchar(500) DEFAULT NULL COMMENT '模板说明',
  `cycle_days` int DEFAULT '30' COMMENT '随访周期（天）',
  `default_content` text COMMENT '默认随访内容模板',
  `review_items` varchar(500) DEFAULT NULL COMMENT '复查提醒项（如"每季度HbA1c;每半年眼底筛查"）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='随访计划模板表';

-- ----------------------------
-- Records of biz_followup_template
-- ----------------------------
BEGIN;
INSERT INTO `biz_followup_template` (`id`, `name`, `description`, `cycle_days`, `default_content`, `review_items`, `create_time`) VALUES (1, '强化管理', '针对血糖控制不佳或新确诊患者，需密切监测', 7, '请记录本周内空腹及餐后血糖波动情况\n用药依从性自评\n足部检查（观察有无破损、感染）', '每季度复查HbA1c;每月复查FPG/2hPG;每半年眼底筛查', '2026-05-19 09:45:07');
INSERT INTO `biz_followup_template` (`id`, `name`, `description`, `cycle_days`, `default_content`, `review_items`, `create_time`) VALUES (2, '常规管理', '针对血糖控制稳定患者，定期跟踪', 30, '请记录近一个月血糖总体控制情况\n体重变化及饮食回顾\n运动习惯评估', '每半年复查HbA1c;每年全面体检;每年眼底筛查', '2026-05-19 09:45:07');
INSERT INTO `biz_followup_template` (`id`, `name`, `description`, `cycle_days`, `default_content`, `review_items`, `create_time`) VALUES (3, '妊娠期管理', '针对妊娠期糖尿病患者，产前产后跟踪', 14, '请记录每日七段血糖值\n血压监测（关注子痫前期风险）\n胎动计数及产科随访记录', '每月复查HbA1c;每两周OGTT筛查;产后6-12周复查血糖', '2026-05-19 09:45:07');
COMMIT;

-- ----------------------------
-- Table structure for biz_health_article
-- ----------------------------
DROP TABLE IF EXISTS `biz_health_article`;
CREATE TABLE `biz_health_article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `category` varchar(50) DEFAULT NULL COMMENT '分类：血糖管理/血压管理/饮食营养/运动康复/用药指导',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要/推送文案',
  `content` text COMMENT '文章正文（HTML 或 Markdown）',
  `tags` varchar(200) DEFAULT NULL COMMENT '匹配标签，逗号分隔',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健康教育文章库';

-- ----------------------------
-- Records of biz_health_article
-- ----------------------------
BEGIN;
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (1, '高血糖应急处理指南', '血糖管理', '当血糖超过16.7 mmol/L时，需立即就医。本文介绍高血糖的识别、应急处理步骤和预防措施。', NULL, '高血糖,酮症酸中毒,胰岛素,应急', '2026-05-19 09:45:07');
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (2, '低血糖急救与预防', '血糖管理', '血糖低于3.9 mmol/L即为低血糖。掌握\"15-15法则\"，随身携带应急糖源。', NULL, '低血糖,急救,糖水,预防', '2026-05-19 09:45:07');
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (3, '血糖波动管理策略', '血糖管理', '血糖忽高忽低比持续高血糖危害更大。从饮食、运动、用药三方面减少波动。', NULL, '血糖波动,饮食管理,运动,用药', '2026-05-19 09:45:07');
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (4, '低血糖预防与饮食调整', '血糖管理', '调整进食时间和食物结构，避免因饮食不当导致的低血糖事件。', NULL, '低血糖,饮食,分餐,碳水化合物', '2026-05-19 09:45:07');
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (5, '高血压急症应对方案', '血压管理', '当血压持续超过180/120 mmHg时，需立即就医。识别高血压急症信号。', NULL, '高血压,急症,头痛,视物模糊', '2026-05-19 09:45:07');
INSERT INTO `biz_health_article` (`id`, `title`, `category`, `summary`, `content`, `tags`, `create_time`) VALUES (6, '血压日常监测与生活干预', '血压管理', '掌握正确测血压方法，通过限盐、减重、运动等生活方式调整控制血压。', NULL, '血压,监测,限盐,DASH饮食,运动', '2026-05-19 09:45:07');
COMMIT;

-- ----------------------------
-- Table structure for biz_health_data
-- ----------------------------
DROP TABLE IF EXISTS `biz_health_data`;
CREATE TABLE `biz_health_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `record_time` datetime NOT NULL COMMENT '测量时间',
  `glucose_value` decimal(10,2) DEFAULT NULL COMMENT '血糖值(mmol/L)',
  `glucose_period` tinyint DEFAULT NULL COMMENT '血糖时段: 1-空腹, 2-早餐后, 3-午餐前...等8时段',
  `systolic_bp` int DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic_bp` int DEFAULT NULL COMMENT '舒张压(mmHg)',
  `heart_rate` int DEFAULT NULL COMMENT '静息心率',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '体重(kg)',
  `exercise_steps` int DEFAULT NULL COMMENT '运动步数',
  `diet_calories` decimal(6,1) DEFAULT NULL COMMENT '饮食热量 (kcal)',
  `medication_name` varchar(100) DEFAULT NULL COMMENT '药品名称',
  `medication_dose` varchar(50) DEFAULT NULL COMMENT '用药剂量',
  `medication_time` varchar(50) DEFAULT NULL COMMENT '用药时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_time` (`patient_id`,`record_time`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院外体征数据表';

-- ----------------------------
-- Records of biz_health_data
-- ----------------------------
BEGIN;
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (17, 10001, '2026-05-19 09:49:59', 5.60, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:49:59');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (18, 10001, '2026-05-19 09:50:31', 17.00, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:50:31');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (19, 10001, '2026-05-19 09:50:42', 18.00, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:50:42');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (20, 10001, '2026-05-19 09:50:50', 19.00, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:50:50');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (21, 10001, '2026-05-19 09:51:02', 19.00, 1, 130, 70, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:51:02');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (22, 10001, '2026-05-19 09:51:10', 6.00, 1, 130, 70, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 09:51:10');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (23, 10001, '2026-05-19 11:02:48', 5.60, 1, 110, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 11:02:48');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (24, 10001, '2026-05-19 15:13:37', 5.60, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 15:13:37');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (25, 40002, '2026-05-19 15:32:40', 5.60, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-19 15:32:40');
INSERT INTO `biz_health_data` (`id`, `patient_id`, `record_time`, `glucose_value`, `glucose_period`, `systolic_bp`, `diastolic_bp`, `heart_rate`, `weight`, `exercise_steps`, `diet_calories`, `medication_name`, `medication_dose`, `medication_time`, `create_time`) VALUES (26, 10001, '2026-05-21 14:31:39', 20.00, 1, 120, 80, 75, 65.50, NULL, NULL, '', '', '', '2026-05-21 14:31:39');
COMMIT;

-- ----------------------------
-- Table structure for biz_health_event
-- ----------------------------
DROP TABLE IF EXISTS `biz_health_event`;
CREATE TABLE `biz_health_event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `event_time` datetime NOT NULL COMMENT '事件发生时间',
  `event_type` tinyint NOT NULL COMMENT '类型: 1-门诊/住院, 2-异常血糖报警, 3-用药变更, 4-随访完成',
  `alert_level` tinyint DEFAULT '0' COMMENT '预警级别: 0-常规, 1-橙色(教育), 2-红色(危急)',
  `description` varchar(255) NOT NULL COMMENT '事件简述(如: 空腹血糖低至3.8, 触发红色预警)',
  `is_handled` tinyint DEFAULT '0' COMMENT '0-未处理, 1-已处理',
  PRIMARY KEY (`id`),
  KEY `idx_patient_time` (`patient_id`,`event_time`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健康事件时间轴表';

-- ----------------------------
-- Records of biz_health_event
-- ----------------------------
BEGIN;
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (16, 10001, '2026-05-19 09:49:59', 4, 0, '患者完成日常体征上报，血糖 5.6 mmol/L。血压 120/80 mmHg。指标均在正常范围内。', 0);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (17, 10001, '2026-05-19 09:50:31', 2, 2, '触发红色危急预警：血糖异常偏高(17 mmol/L)！', 0);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (18, 10001, '2026-05-19 09:50:42', 2, 2, '触发红色危急预警：血糖异常偏高(18 mmol/L)！', 0);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (19, 10001, '2026-05-19 09:50:50', 2, 2, '触发红色危急预警：血糖异常偏高(19 mmol/L)！', 0);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (20, 10001, '2026-05-19 09:51:02', 2, 2, '触发红色危急预警：血糖异常偏高(19 mmol/L)！血压偏高(130/70 mmHg)。', 1);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (21, 10001, '2026-05-19 09:51:10', 2, 1, '触发橙色关注预警：血压偏高(130/70 mmHg)。 建议推送：【血压日常监测与生活干预】', 1);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (22, 10001, '2026-05-19 11:02:48', 4, 0, '患者完成日常体征上报，血糖 5.6 mmol/L。血压 110/80 mmHg。指标均在正常范围内。', NULL);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (23, 10001, '2026-05-19 15:13:37', 4, 0, '患者完成日常体征上报，血糖 5.6 mmol/L。血压 120/80 mmHg。指标均在正常范围内。', NULL);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (24, 40002, '2026-05-19 15:32:40', 4, 0, '患者完成日常体征上报，血糖 5.6 mmol/L。血压 120/80 mmHg。指标均在正常范围内。', NULL);
INSERT INTO `biz_health_event` (`id`, `patient_id`, `event_time`, `event_type`, `alert_level`, `description`, `is_handled`) VALUES (25, 10001, '2026-05-21 14:31:39', 2, 2, '触发红色危急预警：血糖异常偏高(20 mmol/L)！', 0);
COMMIT;

-- ----------------------------
-- Table structure for biz_message
-- ----------------------------
DROP TABLE IF EXISTS `biz_message`;
CREATE TABLE `biz_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `content` varchar(500) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_conversation` (`sender_id`,`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='医患在线沟通消息表';

-- ----------------------------
-- Records of biz_message
-- ----------------------------
BEGIN;
INSERT INTO `biz_message` (`id`, `sender_id`, `receiver_id`, `content`, `create_time`) VALUES (1, 10001, 20001, '你好', '2026-05-19 14:53:48');
INSERT INTO `biz_message` (`id`, `sender_id`, `receiver_id`, `content`, `create_time`) VALUES (2, 20001, 10001, '收到', '2026-05-19 14:54:09');
INSERT INTO `biz_message` (`id`, `sender_id`, `receiver_id`, `content`, `create_time`) VALUES (3, 40002, 20001, '王医生在吗', '2026-05-19 15:34:52');
INSERT INTO `biz_message` (`id`, `sender_id`, `receiver_id`, `content`, `create_time`) VALUES (4, 20001, 40002, '在的', '2026-05-19 15:40:58');
COMMIT;

-- ----------------------------
-- Table structure for biz_patient_profile
-- ----------------------------
DROP TABLE IF EXISTS `biz_patient_profile`;
CREATE TABLE `biz_patient_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关联 sys_user 的 id',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `diabetes_type` int DEFAULT NULL COMMENT '糖尿病类型：1-1型, 2-2型, 3-妊娠期, 4-其他',
  `complications` varchar(255) DEFAULT NULL COMMENT '并发症史，多个用逗号分隔',
  `fpg` decimal(4,1) DEFAULT NULL COMMENT '空腹血糖',
  `two_hpg` decimal(5,1) DEFAULT NULL COMMENT '餐后2h血糖',
  `ga` decimal(5,1) DEFAULT NULL COMMENT '糖化白蛋白',
  `hba1c` decimal(4,1) DEFAULT NULL COMMENT '糖化血红蛋白',
  `cholesterol` decimal(4,1) DEFAULT NULL COMMENT '总胆固醇',
  `triglyceride` decimal(4,1) DEFAULT NULL COMMENT '甘油三酯',
  `ldl` decimal(5,1) DEFAULT NULL COMMENT 'LDL-C',
  `hdl` decimal(5,1) DEFAULT NULL COMMENT 'HDL-C',
  `creatinine` decimal(6,1) DEFAULT NULL COMMENT '肌酐',
  `urinary_microalbumin` decimal(6,1) DEFAULT NULL COMMENT '尿微量白蛋白',
  `height` decimal(5,1) DEFAULT NULL COMMENT '身高 (cm)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='患者医疗档案表';

-- ----------------------------
-- Records of biz_patient_profile
-- ----------------------------
BEGIN;
INSERT INTO `biz_patient_profile` (`id`, `user_id`, `id_card`, `birth_date`, `phone`, `diabetes_type`, `complications`, `fpg`, `two_hpg`, `ga`, `hba1c`, `cholesterol`, `triglyceride`, `ldl`, `hdl`, `creatinine`, `urinary_microalbumin`, `height`, `create_time`, `update_time`) VALUES (2, 10001, '110105199001011234', '1990-01-01', '13800138000', 2, '糖尿病视网膜病变', 7.5, 2.0, 13.0, 8.2, 4.0, 1.0, 2.0, 1.0, 50.0, 10.0, 170.0, '2026-05-19 09:45:07', '2026-05-19 15:13:29');
INSERT INTO `biz_patient_profile` (`id`, `user_id`, `id_card`, `birth_date`, `phone`, `diabetes_type`, `complications`, `fpg`, `two_hpg`, `ga`, `hba1c`, `cholesterol`, `triglyceride`, `ldl`, `hdl`, `creatinine`, `urinary_microalbumin`, `height`, `create_time`, `update_time`) VALUES (3, 40002, '111111111111111111', '1981-05-19', '13012345678', 1, '无', 4.0, 2.0, 12.0, 4.0, 4.0, 1.0, 2.0, 1.0, 50.0, 2.0, 160.0, '2026-05-19 15:34:24', '2026-05-19 15:34:24');
COMMIT;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `action` varchar(100) NOT NULL COMMENT '操作动作(如: 修改随访计划)',
  `target_data` text COMMENT '修改前后的敏感数据/入参',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';

-- ----------------------------
-- Records of sys_log
-- ----------------------------
BEGIN;
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (15, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=5.6, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:46:28');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (16, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=5.6, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:49:59');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (17, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=17, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:50:31');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (18, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=18, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:50:42');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (19, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=19, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:50:50');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (20, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=19, glucosePeriod=1, systolicBp=130, diastolicBp=70, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:51:02');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (21, 10001, '患者上报日常体征数据', 'Method: com.antigravity.diabetes.controller.HealthDataController.uploadHealthData() | Params: [HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=6, glucosePeriod=1, systolicBp=130, diastolicBp=70, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=)] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:51:10');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (22, 20001, '医生制定新的随访计划', 'Method: com.antigravity.diabetes.controller.FollowupPlanController.createPlan() | Params: [FollowupPlanDTO(patientId=10001, planDate=2026-05-26, content=请记录本周内空腹及餐后血糖波动情况\n用药依从性自评\n足部检查（观察有无破损、感染）), org.apache.catalina.connector.RequestFacade@7b60a8ee] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:51:55');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (23, 20001, '更新随访计划执行状态', 'Method: com.antigravity.diabetes.controller.FollowupPlanController.updateStatus() | Params: [2, 1] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:52:01');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (24, 20001, '配置患者个性化预警阈值', 'Method: com.antigravity.diabetes.controller.AlertThresholdController.saveThreshold() | Params: [AlertThresholdDTO(patientId=10001, glucoseLow=3.9, glucoseWarnLow=4.5, glucoseWarnHigh=10, glucoseHigh=16.7, systolicMax=140, diastolicMax=90, systolicWarn=130, diastolicWarn=85, notes=), org.apache.catalina.connector.RequestFacade@be59d03] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 09:53:27');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (25, 20001, '标记紧急事件已处理', 'Method: com.antigravity.diabetes.controller.HealthEventController.handleEvent() | Params: [20] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 10:03:06');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (26, 20001, '标记紧急事件已处理', 'Method: com.antigravity.diabetes.controller.HealthEventController.handleEvent() | Params: [21] | IP: 0:0:0:0:0:0:0:1', '2026-05-19 10:05:50');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (27, 20001, '医生制定新的随访计划', 'CREATE | createPlan | FollowupPlanDTO(patientId=10001, planDate=2026-05-19, content=【高血糖应急处理指南】\n当血糖超过16.7 mmol/L时，需立即就医。本文介绍高血糖的识别、应急处理步骤和预防措施。\n\n—— 来自你的主管医生) | 0:0:0:0:0:0:0:1', '2026-05-19 10:26:57');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (28, 10001, '患者上报日常体征数据', 'CREATE | uploadHealthData | HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=5.6, glucosePeriod=1, systolicBp=110, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=) | 0:0:0:0:0:0:0:1', '2026-05-19 11:02:48');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (29, 10001, '更新患者档案', 'CREATE | updateProfile | PatientProfileDTO(idCard=110105199001011234, birthDate=1990-01-01, phone=13800138000, diabetesType=2, complications=糖尿病视网膜病变, fpg=7.5, twoHpg=null, ga=null, hba1c=8.2, cholesterol=null, triglyceride=null, ldl=null, hdl=null, creatinine=null, urinaryMicroalbumin=null, height=170) | 0:0:0:0:0:0:0:1', '2026-05-19 15:12:17');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (30, 10001, '更新患者档案', 'CREATE | updateProfile | PatientProfileDTO(idCard=110105199001011234, birthDate=1990-01-01, phone=13800138000, diabetesType=2, complications=糖尿病视网膜病变, fpg=7.5, twoHpg=2, ga=13, hba1c=8.2, cholesterol=4, triglyceride=1, ldl=2, hdl=1, creatinine=50, urinaryMicroalbumin=10, height=170) | 0:0:0:0:0:0:0:1', '2026-05-19 15:13:30');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (31, 10001, '患者上报日常体征数据', 'CREATE | uploadHealthData | HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=5.6, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=) | 0:0:0:0:0:0:0:1', '2026-05-19 15:13:37');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (32, 40002, '患者上报日常体征数据', 'CREATE | uploadHealthData | HealthDataDTO(patientId=40002, recordTime=null, glucoseValue=5.6, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=) | 0:0:0:0:0:0:0:1', '2026-05-19 15:32:40');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (33, 40002, '更新患者档案', 'CREATE | updateProfile | PatientProfileDTO(idCard=111111111111111111, birthDate=1981-05-19, phone=13012345678, diabetesType=1, complications=无, fpg=4, twoHpg=2, ga=12, hba1c=4, cholesterol=4, triglyceride=1, ldl=2, hdl=1, creatinine=50, urinaryMicroalbumin=2, height=160) | 0:0:0:0:0:0:0:1', '2026-05-19 15:34:25');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (34, 10001, '患者上报日常体征数据', 'CREATE | uploadHealthData | HealthDataDTO(patientId=10001, recordTime=null, glucoseValue=20, glucosePeriod=1, systolicBp=120, diastolicBp=80, heartRate=75, weight=65.5, exerciseSteps=null, dietCalories=null, medicationName=, medicationDose=, medicationTime=) | 0:0:0:0:0:0:0:1', '2026-05-21 14:31:39');
INSERT INTO `sys_log` (`id`, `operator_id`, `action`, `target_data`, `create_time`) VALUES (35, 20001, '医生制定新的随访计划', 'CREATE | createPlan | FollowupPlanDTO(patientId=40002, planDate=2026-05-21, content=【低血糖预防与饮食调整】\n调整进食时间和食物结构，避免因饮食不当导致的低血糖事件。\n\n—— 来自你的主管医生) | 0:0:0:0:0:0:0:1', '2026-05-21 14:32:17');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号/手机号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `role_type` tinyint NOT NULL COMMENT '角色类型: 1-管理员, 2-医生, 3-护士, 4-患者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-正常, 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=40003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_type`, `create_time`, `is_deleted`) VALUES (10001, 'patient', '$2a$10$6DdRJ2rJoa8l.7PQ1baUZeP1qGod8sz8imotmbtToAYVZ4755GhNu', '李患者', 4, '2026-05-19 09:45:50', 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_type`, `create_time`, `is_deleted`) VALUES (20001, 'doctor', '$2a$10$lTGHvrgrgA/vuRK2bE8dH.z.MybKdX2owTEvkwjlHcUCNFFnfyuKK', '王医生', 2, '2026-05-19 09:45:50', 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_type`, `create_time`, `is_deleted`) VALUES (30001, 'admin', '$2a$10$NQjQMQNJfRX7UE.uUdFCsOORZs77pEaKiQDLsd0OOvT8epfaTGPmi', '系统管理员', 1, '2026-05-19 10:22:03', 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_type`, `create_time`, `is_deleted`) VALUES (40001, 'nurse', '$2a$10$a/gIh882ul/9cng4jDOVfewhx3rhC/Ckn3IDSpGBsmIV8b/TIEGmS', '张护士', 3, '2026-05-19 10:22:03', 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_type`, `create_time`, `is_deleted`) VALUES (40002, 'test', '$2a$10$wza7NmFuEZaRyeHgItrpPueaHEkRERAfzA2aAZVpFAKgn/Wjq8CAC', 'test', 4, '2026-05-19 15:32:25', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
