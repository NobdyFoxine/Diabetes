package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BizFollowupPlan {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate planDate;
    private String content;
    private Integer status; // 0-待执行, 1-已完成, 2-已取消
    private LocalDateTime createTime;
}
