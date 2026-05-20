package com.antigravity.diabetes.dto;

import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class FollowupPlanDTO {
    private Long patientId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;
    private String content;
}
