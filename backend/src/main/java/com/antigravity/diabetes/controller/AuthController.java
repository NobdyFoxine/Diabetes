package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.dto.LoginDTO;
import com.antigravity.diabetes.service.AuthService;
import com.antigravity.diabetes.vo.CommonResult;
import com.antigravity.diabetes.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.antigravity.diabetes.entity.SysUser;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证管理", description = "用户登录、注册接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名密码登录，返回 JWT Token")
    public CommonResult<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        try {
            return CommonResult.success(authService.login(dto));
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户，默认角色为患者(roleType=4)")
    public CommonResult<?> register(@RequestBody SysUser user) {
        try {
            if (user.getRoleType() == null) user.setRoleType(4);
            authService.register(user);
            return CommonResult.success("注册成功，请登录");
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }
}
