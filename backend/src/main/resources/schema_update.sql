-- ============================================================
-- 糖尿病患者院外管理系统 - 完整数据库建表脚本
-- 数据库名: shixunxaingmu
-- ============================================================

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密后的密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role_type INT NOT NULL DEFAULT 4 COMMENT '角色：1-管理员, 2-医生, 3-护士, 4-患者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
    UNIQUE KEY uk_username (username)
) COMMENT='系统用户表';

-- 2. 患者医疗档案表（含院内检验基线数据）
CREATE TABLE IF NOT EXISTS biz_patient_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '关联 sys_user.id',
    id_card VARCHAR(18) COMMENT '身份证号',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '联系电话',
    diabetes_type INT COMMENT '糖尿病分型：1-1型, 2-2型, 3-妊娠期, 4-其他',
    complications VARCHAR(255) COMMENT '并发症史，逗号分隔',
    -- 院内检验基线数据
    fpg DECIMAL(5,1) COMMENT '空腹血糖 FPG (mmol/L)',
    two_hpg DECIMAL(5,1) COMMENT '餐后2小时血糖 2hPG (mmol/L)',
    ga DECIMAL(5,1) COMMENT '糖化白蛋白 GA (%)',
    hba1c DECIMAL(4,1) COMMENT '糖化血红蛋白 HbA1c (%)',
    cholesterol DECIMAL(5,1) COMMENT '总胆固醇 TC (mmol/L)',
    triglyceride DECIMAL(5,1) COMMENT '甘油三酯 TG (mmol/L)',
    ldl DECIMAL(5,1) COMMENT '低密度脂蛋白 LDL-C (mmol/L)',
    hdl DECIMAL(5,1) COMMENT '高密度脂蛋白 HDL-C (mmol/L)',
    creatinine DECIMAL(6,1) COMMENT '肌酐 Cr (umol/L)',
    urinary_microalbumin DECIMAL(6,1) COMMENT '尿微量白蛋白 MAU (mg/L)',
    height DECIMAL(5,1) COMMENT '身高 (cm)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
) COMMENT='患者医疗档案表';

-- 3. 体征数据表（患者自报数据）
CREATE TABLE IF NOT EXISTS biz_health_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '关联 sys_user.id（患者）',
    record_time DATETIME NOT NULL COMMENT '测量/记录时间',
    glucose_value DECIMAL(4,1) COMMENT '血糖值 (mmol/L)',
    glucose_period INT COMMENT '测量时段：1-空腹, 2-早餐后, 3-午餐前, 4-午餐后, 5-晚餐前, 6-晚餐后, 7-睡前, 8-凌晨',
    systolic_bp INT COMMENT '收缩压 (mmHg)',
    diastolic_bp INT COMMENT '舒张压 (mmHg)',
    heart_rate INT COMMENT '心率 (bpm)',
    weight DECIMAL(5,1) COMMENT '体重 (kg)',
    exercise_steps INT COMMENT '运动步数',
    diet_calories DECIMAL(6,1) COMMENT '饮食热量 (kcal)',
    medication_name VARCHAR(100) COMMENT '药品名称',
    medication_dose VARCHAR(50) COMMENT '用药剂量',
    medication_time VARCHAR(50) COMMENT '用药时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_time (patient_id, record_time)
) COMMENT='体征数据表';

-- 4. 健康事件表
CREATE TABLE IF NOT EXISTS biz_health_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '关联 sys_user.id（患者）',
    event_time DATETIME NOT NULL COMMENT '事件发生时间',
    event_type INT NOT NULL COMMENT '事件类型：1-门诊/住院, 2-异常生理报警, 3-用药方案变更, 4-随访记录完成',
    alert_level INT DEFAULT 0 COMMENT '预警级别：0-常规, 1-橙色预警, 2-红色报警',
    description VARCHAR(500) COMMENT '事件描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_event (patient_id, event_time)
) COMMENT='健康事件表';

-- 5. 个性化预警阈值表
CREATE TABLE IF NOT EXISTS biz_alert_threshold (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '关联 sys_user.id（患者）',
    glucose_low DECIMAL(4,1) DEFAULT 3.9 COMMENT '血糖过低临界值 (mmol/L)',
    glucose_warn_low DECIMAL(4,1) DEFAULT 4.5 COMMENT '血糖偏低关注值',
    glucose_warn_high DECIMAL(4,1) DEFAULT 10.0 COMMENT '血糖偏高关注值',
    glucose_high DECIMAL(4,1) DEFAULT 16.7 COMMENT '血糖过高临界值',
    systolic_max INT DEFAULT 140 COMMENT '收缩压最高 (mmHg)',
    diastolic_max INT DEFAULT 90 COMMENT '舒张压最高 (mmHg)',
    systolic_warn INT DEFAULT 130 COMMENT '收缩压关注值',
    diastolic_warn INT DEFAULT 85 COMMENT '舒张压关注值',
    notes VARCHAR(200) COMMENT '备注（如"高龄患者降糖方案"）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_patient_id (patient_id)
) COMMENT='患者个性化预警阈值表';

-- 6. 健康教育文章库
CREATE TABLE IF NOT EXISTS biz_health_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    category VARCHAR(50) COMMENT '分类：血糖管理/血压管理/饮食营养/运动康复/用药指导',
    summary VARCHAR(500) COMMENT '摘要/推送文案',
    content TEXT COMMENT '文章正文（HTML 或 Markdown）',
    tags VARCHAR(200) COMMENT '匹配标签，逗号分隔',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='健康教育文章库';

-- 7. 随访计划模板表
CREATE TABLE IF NOT EXISTS biz_followup_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '模板名称（强化管理/常规管理）',
    description VARCHAR(500) COMMENT '模板说明',
    cycle_days INT DEFAULT 30 COMMENT '随访周期（天）',
    default_content TEXT COMMENT '默认随访内容模板',
    review_items VARCHAR(500) COMMENT '复查提醒项（如"每季度HbA1c;每半年眼底筛查"）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='随访计划模板表';

-- 8. 医患在线沟通消息表
CREATE TABLE IF NOT EXISTS biz_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL COMMENT '发送者 sys_user.id',
    receiver_id BIGINT NOT NULL COMMENT '接收者 sys_user.id',
    content VARCHAR(500) NOT NULL COMMENT '消息正文',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation (sender_id, receiver_id)
) COMMENT='医患在线沟通消息表';

-- 原随访计划表（序号顺延）
-- 5. ...
CREATE TABLE IF NOT EXISTS biz_followup_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '关联 sys_user.id（患者）',
    doctor_id BIGINT COMMENT '关联 sys_user.id（医生）',
    plan_date DATE NOT NULL COMMENT '计划执行日期',
    content VARCHAR(500) NOT NULL COMMENT '随访/干预内容',
    status INT DEFAULT 0 COMMENT '状态：0-待执行, 1-已完成, 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id)
) COMMENT='随访计划表';

-- 6. 系统操作日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT COMMENT '操作者 sys_user.id',
    action VARCHAR(200) NOT NULL COMMENT '操作内容（来自 @LogOperation 注解）',
    target_data TEXT COMMENT '请求参数/详情（JSON 序列化）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operator (operator_id),
    INDEX idx_time (create_time)
) COMMENT='系统操作日志表';

-- ============================================================
-- 初始测试数据（Reset 时可删除旧号重新开始）
-- ============================================================

-- 删除旧测试数据
DELETE FROM biz_followup_plan WHERE patient_id = 10001 OR doctor_id = 20001;
DELETE FROM biz_health_event WHERE patient_id = 10001;
DELETE FROM biz_health_data WHERE patient_id = 10001;
DELETE FROM biz_patient_profile WHERE user_id IN (10001, 20001);
DELETE FROM sys_log WHERE operator_id IN (10001, 20001);
DELETE FROM sys_user WHERE id IN (10001, 20001);

-- ============================================================
-- ALTER TABLE 补齐缺失列
-- 注意：报 Duplicate column name 表示该列已存，忽略继续
-- ============================================================

-- biz_patient_profile: 补齐 Phase 1 新增的 6 个检验字段
ALTER TABLE biz_patient_profile ADD COLUMN two_hpg DECIMAL(5,1) COMMENT '餐后2小时血糖 2hPG (mmol/L)' AFTER fpg;
ALTER TABLE biz_patient_profile ADD COLUMN ga DECIMAL(5,1) COMMENT '糖化白蛋白 GA (%)' AFTER two_hpg;
ALTER TABLE biz_patient_profile ADD COLUMN ldl DECIMAL(5,1) COMMENT '低密度脂蛋白 LDL-C (mmol/L)' AFTER triglyceride;
ALTER TABLE biz_patient_profile ADD COLUMN hdl DECIMAL(5,1) COMMENT '高密度脂蛋白 HDL-C (mmol/L)' AFTER ldl;
ALTER TABLE biz_patient_profile ADD COLUMN creatinine DECIMAL(6,1) COMMENT '肌酐 Cr (umol/L)' AFTER hdl;
ALTER TABLE biz_patient_profile ADD COLUMN urinary_microalbumin DECIMAL(6,1) COMMENT '尿微量白蛋白 MAU (mg/L)' AFTER creatinine;

-- biz_patient_profile: 补齐身高字段（BMI 计算用）
ALTER TABLE biz_patient_profile ADD COLUMN height DECIMAL(5,1) COMMENT '身高 (cm)' AFTER urinary_microalbumin;

-- biz_health_event: 补齐紧急事件处理状态字段
ALTER TABLE biz_health_event ADD COLUMN is_handled TINYINT DEFAULT 0 COMMENT '处理状态：0-未处理, 1-已处理' AFTER description;

-- biz_health_data: 补齐 Phase 2 新增的 5 个生活方式字段
ALTER TABLE biz_health_data ADD COLUMN exercise_steps INT COMMENT '运动步数' AFTER weight;
ALTER TABLE biz_health_data ADD COLUMN diet_calories DECIMAL(6,1) COMMENT '饮食热量 (kcal)' AFTER exercise_steps;
ALTER TABLE biz_health_data ADD COLUMN medication_name VARCHAR(100) COMMENT '药品名称' AFTER diet_calories;
ALTER TABLE biz_health_data ADD COLUMN medication_dose VARCHAR(50) COMMENT '用药剂量' AFTER medication_name;
ALTER TABLE biz_health_data ADD COLUMN medication_time VARCHAR(50) COMMENT '用药时间' AFTER medication_dose;

-- 测试用户由 MockDataRunner 在应用启动时自动创建（BCrypt 加密密码）
-- 此处仅提供患者档案初始数据
INSERT IGNORE INTO biz_patient_profile (user_id, id_card, birth_date, phone, diabetes_type, complications, fpg, hba1c)
VALUES (10001, '110105199001011234', '1990-01-01', '13800138000', 2, '糖尿病视网膜病变', 7.5, 8.2);

-- 随访模板种子数据
INSERT IGNORE INTO biz_followup_template (id, name, description, cycle_days, default_content, review_items) VALUES
(1, '强化管理', '针对血糖控制不佳或新确诊患者，需密切监测', 7, '请记录本周内空腹及餐后血糖波动情况\n用药依从性自评\n足部检查（观察有无破损、感染）', '每季度复查HbA1c;每月复查FPG/2hPG;每半年眼底筛查'),
(2, '常规管理', '针对血糖控制稳定患者，定期跟踪', 30, '请记录近一个月血糖总体控制情况\n体重变化及饮食回顾\n运动习惯评估', '每半年复查HbA1c;每年全面体检;每年眼底筛查'),
(3, '妊娠期管理', '针对妊娠期糖尿病患者，产前产后跟踪', 14, '请记录每日七段血糖值\n血压监测（关注子痫前期风险）\n胎动计数及产科随访记录', '每月复查HbA1c;每两周OGTT筛查;产后6-12周复查血糖');

-- 健康教育文章种子数据
INSERT IGNORE INTO biz_health_article (id, title, category, summary, tags) VALUES
(1, '高血糖应急处理指南', '血糖管理', '当血糖超过16.7 mmol/L时，需立即就医。本文介绍高血糖的识别、应急处理步骤和预防措施。', '高血糖,酮症酸中毒,胰岛素,应急'),
(2, '低血糖急救与预防', '血糖管理', '血糖低于3.9 mmol/L即为低血糖。掌握\"15-15法则\"，随身携带应急糖源。', '低血糖,急救,糖水,预防'),
(3, '血糖波动管理策略', '血糖管理', '血糖忽高忽低比持续高血糖危害更大。从饮食、运动、用药三方面减少波动。', '血糖波动,饮食管理,运动,用药'),
(4, '低血糖预防与饮食调整', '血糖管理', '调整进食时间和食物结构，避免因饮食不当导致的低血糖事件。', '低血糖,饮食,分餐,碳水化合物'),
(5, '高血压急症应对方案', '血压管理', '当血压持续超过180/120 mmHg时，需立即就医。识别高血压急症信号。', '高血压,急症,头痛,视物模糊'),
(6, '血压日常监测与生活干预', '血压管理', '掌握正确测血压方法，通过限盐、减重、运动等生活方式调整控制血压。', '血压,监测,限盐,DASH饮食,运动');
