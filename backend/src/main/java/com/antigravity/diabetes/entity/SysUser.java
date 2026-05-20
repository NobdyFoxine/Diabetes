package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Integer roleType; // 1-管理员, 2-医生, 3-护士, 4-患者
    private LocalDateTime createTime;
    private Integer isDeleted;
}
