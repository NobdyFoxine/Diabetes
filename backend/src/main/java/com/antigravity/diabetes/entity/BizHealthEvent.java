package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizHealthEvent {
    private Long id;
    private Long patientId;
    private LocalDateTime eventTime;
    private Integer eventType;
    private Integer alertLevel;
    private String description;
    private Integer isHandled; // 0-未处理, 1-已处理
}
