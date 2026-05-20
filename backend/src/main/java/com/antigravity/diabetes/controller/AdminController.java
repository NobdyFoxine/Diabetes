package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.entity.SysUser;
import com.antigravity.diabetes.mapper.SysUserMapper;
import com.antigravity.diabetes.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "管理员管理", description = "系统管理员用户管理与角色分配")
public class AdminController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/users")
    @Operation(summary = "查询所有用户", description = "获取系统全部注册用户列表")
    public CommonResult<List<SysUser>> listUsers() {
        return CommonResult.success(sysUserMapper.selectAll());
    }

    @PutMapping("/user/{id}/role")
    @LogOperation("更改用户角色")
    @Operation(summary = "修改用户角色", description = "管理员修改指定用户的角色类型(1-管理员,2-医生,3-护士,4-患者)")
    public CommonResult<?> updateRole(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer roleType = body.get("roleType");
        if (roleType == null || roleType < 1 || roleType > 4) {
            return CommonResult.error("角色类型无效");
        }
        sysUserMapper.updateRole(id, roleType);
        return CommonResult.success("角色已更新");
    }
}
