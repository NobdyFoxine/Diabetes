package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizFollowupTemplate {
    private Long id;
    private String name;
    private String description;
    private Integer cycleDays;
    private String defaultContent;
    private String reviewItems;
    private LocalDateTime createTime;
}
