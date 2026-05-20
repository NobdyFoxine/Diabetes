package com.antigravity.diabetes.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthEventVO {
    private Long id;
    private Long patientId;
    private LocalDateTime eventTime;
    private Integer eventType; // 1-门诊/住院, 2-异常血糖报警, 3-用药变更, 4-随访完成
    private Integer alertLevel; // 0-常规, 1-橙色, 2-红色
    private String description;
}
