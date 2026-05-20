package com.antigravity.diabetes.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PatientProfileDTO {
    private String idCard;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String phone;
    private Integer diabetesType;
    private String complications;
    private BigDecimal fpg;
    private BigDecimal twoHpg;
    private BigDecimal ga;
    private BigDecimal hba1c;
    private BigDecimal cholesterol;
    private BigDecimal triglyceride;
    private BigDecimal ldl;
    private BigDecimal hdl;
    private BigDecimal creatinine;
    private BigDecimal urinaryMicroalbumin;
    private BigDecimal height;
}
