package com.antigravity.diabetes.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String realName;
    private Integer roleType;
}
