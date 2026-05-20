package com.antigravity.diabetes.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class HealthDataDTO {
    private Long patientId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordTime;
    private BigDecimal glucoseValue;
    private Integer glucosePeriod;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer heartRate;
    private BigDecimal weight;
    private Integer exerciseSteps;
    private BigDecimal dietCalories;
    private String medicationName;
    private String medicationDose;
    private String medicationTime;
}
