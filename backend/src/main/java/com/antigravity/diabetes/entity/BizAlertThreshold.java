package com.antigravity.diabetes.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BizAlertThreshold {
    private Long id;
    private Long patientId;
    private BigDecimal glucoseLow;     // 血糖过低临界值（默认 3.9）
    private BigDecimal glucoseWarnLow;  // 血糖偏低关注值（默认 4.5）
    private BigDecimal glucoseWarnHigh; // 血糖偏高关注值（默认 10.0）
    private BigDecimal glucoseHigh;     // 血糖过高临界值（默认 16.7）
    private Integer systolicMax;        // 收缩压最高（默认 140）
    private Integer diastolicMax;       // 舒张压最高（默认 90）
    private Integer systolicWarn;       // 收缩压关注（默认 130）
    private Integer diastolicWarn;      // 舒张压关注（默认 85）
    private String notes;               // 备注（如"高龄患者降糖方案"）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
