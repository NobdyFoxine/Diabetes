package com.antigravity.diabetes.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BizPatientProfile {
    private Long id;
    private Long userId; // 关联 sys_user
    private String idCard;
    private LocalDate birthDate;
    private String phone;
    private Integer diabetesType; // 1-1型, 2-2型, 3-妊娠期, 4-其他
    private String complications;
    private BigDecimal fpg; // 空腹血糖
    private BigDecimal twoHpg; // 餐后2小时血糖
    private BigDecimal ga; // 糖化白蛋白
    private BigDecimal hba1c; // 糖化血红蛋白
    private BigDecimal cholesterol; // 总胆固醇
    private BigDecimal triglyceride; // 甘油三酯
    private BigDecimal ldl; // 低密度脂蛋白
    private BigDecimal hdl; // 高密度脂蛋白
    private BigDecimal creatinine; // 肌酐
    private BigDecimal urinaryMicroalbumin; // 尿微量白蛋白
    private BigDecimal height; // 身高 (cm)，用于 BMI 计算
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
