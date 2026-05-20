package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysLog {
    private Long id;
    private Long operatorId; // 操作人ID (对应 operator_id)
    private String action; // 操作说明 (对应 action)
    private String targetData; // 目标数据/请求详情 (对应 target_data)
    private LocalDateTime createTime; // 操作时间
}
