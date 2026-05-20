package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.dto.AlertThresholdDTO;
import com.antigravity.diabetes.entity.BizAlertThreshold;
import com.antigravity.diabetes.mapper.AlertThresholdMapper;
import com.antigravity.diabetes.vo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/doctor/alert-threshold")
@Tag(name = "个性化预警阈值", description = "医生为患者配置个性化血糖、血压预警阈值")
public class AlertThresholdController {

    @Autowired
    private AlertThresholdMapper alertThresholdMapper;

    @GetMapping("/{patientId}")
    @Operation(summary = "查询患者阈值", description = "获取指定患者的个性化预警阈值配置")
    public CommonResult<BizAlertThreshold> getThreshold(@PathVariable Long patientId) {
        BizAlertThreshold t = alertThresholdMapper.selectByPatientId(patientId);
        return CommonResult.success(t);
    }

    @PostMapping("/save")
    @LogOperation("配置患者个性化预警阈值")
    @Operation(summary = "保存阈值配置", description = "新建或更新患者的预警阈值参数")
    public CommonResult<?> saveThreshold(@RequestBody AlertThresholdDTO dto, HttpServletRequest request) {
        BizAlertThreshold t = alertThresholdMapper.selectByPatientId(dto.getPatientId());
        BizAlertThreshold entity = new BizAlertThreshold();
        BeanUtils.copyProperties(dto, entity);
        if (t == null) {
            alertThresholdMapper.insert(entity);
        } else {
            alertThresholdMapper.updateByPatientId(entity);
        }
        return CommonResult.success("阈值配置已保存");
    }
}
