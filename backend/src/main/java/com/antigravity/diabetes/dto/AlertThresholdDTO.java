package com.antigravity.diabetes.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AlertThresholdDTO {
    private Long patientId;
    private BigDecimal glucoseLow;
    private BigDecimal glucoseWarnLow;
    private BigDecimal glucoseWarnHigh;
    private BigDecimal glucoseHigh;
    private Integer systolicMax;
    private Integer diastolicMax;
    private Integer systolicWarn;
    private Integer diastolicWarn;
    private String notes;
}
