package com.antigravity.diabetes.vo;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class SysLogVO {
    private Long id;
    private Long operatorId;
    private String operatorName;
    private Integer roleType;
    private String action;
    private String targetData;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
