package com.antigravity.diabetes.service;

import com.antigravity.diabetes.dto.LoginDTO;
import com.antigravity.diabetes.vo.LoginVO;

import com.antigravity.diabetes.entity.SysUser;

public interface AuthService {
    LoginVO login(LoginDTO dto);
    void register(SysUser user);
}
