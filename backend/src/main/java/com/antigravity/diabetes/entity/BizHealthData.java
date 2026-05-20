package com.antigravity.diabetes.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BizHealthData {
    private Long id;
    private Long patientId;
    private LocalDateTime recordTime;
    private BigDecimal glucoseValue;
    private Integer glucosePeriod;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer heartRate;
    private BigDecimal weight;
    private Integer exerciseSteps;    // 运动步数
    private BigDecimal dietCalories;  // 饮食热量 (kcal)
    private String medicationName;    // 药品名称
    private String medicationDose;    // 用药剂量
    private String medicationTime;    // 用药时间（如"早餐后"）
    private LocalDateTime createTime;
}
