package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.dto.PatientProfileDTO;
import com.antigravity.diabetes.entity.BizPatientProfile;
import com.antigravity.diabetes.mapper.PatientProfileMapper;
import com.antigravity.diabetes.vo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/patient/profile")
@Tag(name = "患者档案管理", description = "患者基础信息、糖尿病分型、院内检验数据管理")
public class PatientProfileController {

    @Autowired
    private PatientProfileMapper patientProfileMapper;

    @GetMapping("/mine")
    @Operation(summary = "获取我的档案", description = "根据当前登录Token获取患者本人的健康档案")
    public CommonResult<BizPatientProfile> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return CommonResult.error("用户未登录");
        return CommonResult.success(patientProfileMapper.selectByUserId(userId));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "按用户ID查档案", description = "医生根据患者userId查看其健康档案")
    public CommonResult<BizPatientProfile> getByUserId(@PathVariable Long userId) {
        return CommonResult.success(patientProfileMapper.selectByUserId(userId));
    }

    @PostMapping("/update")
    @LogOperation("更新患者档案")
    @Operation(summary = "更新患者档案", description = "新建或更新患者的基础信息与检验基线数据")
    public CommonResult<?> updateProfile(@RequestBody PatientProfileDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return CommonResult.error("用户未登录");
        }
        BizPatientProfile existing = patientProfileMapper.selectByUserId(userId);
        BizPatientProfile profile = new BizPatientProfile();
        BeanUtils.copyProperties(dto, profile);
        profile.setUserId(userId);

        if (existing == null) {
            patientProfileMapper.insert(profile);
        } else {
            patientProfileMapper.updateByUserId(profile);
        }
        return CommonResult.success("患者档案更新成功");
    }
}
