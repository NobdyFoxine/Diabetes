package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.service.HealthEventService;
import com.antigravity.diabetes.vo.CommonResult;
import com.antigravity.diabetes.vo.HealthEventVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient/health-events")
@Tag(name = "预警与健康事件", description = "健康事件时间轴、预警统计、警报处理")
public class HealthEventController {

    @Autowired
    private HealthEventService healthEventService;

    @Autowired
    private com.antigravity.diabetes.mapper.HealthEventMapper healthEventMapper;

    @GetMapping("/{patientId}")
    @Operation(summary = "患者事件时间轴", description = "获取指定患者的全部健康事件列表")
    public CommonResult<List<HealthEventVO>> getPatientEvents(@PathVariable("patientId") Long patientId) {
        List<HealthEventVO> events = healthEventService.getEventsByPatientId(patientId);
        return CommonResult.success(events);
    }

    @GetMapping("/alerts/recent")
    @Operation(summary = "最近预警列表", description = "获取系统最近的异常预警事件")
    public CommonResult<List<com.antigravity.diabetes.entity.BizHealthEvent>> getRecentAlerts() {
        return CommonResult.success(healthEventMapper.selectRecentAlerts());
    }

    @GetMapping("/alerts/stats")
    @Operation(summary = "预警统计数据", description = "获取待处理和已处理的预警数量")
    public CommonResult<Map<String, Integer>> getAlertStats() {
        return CommonResult.success(Map.of(
            "unhandled", healthEventMapper.countUnhandled(),
            "handled", healthEventMapper.countHandled()
        ));
    }

    @PutMapping("/{id}/handle")
    @LogOperation("标记紧急事件已处理")
    @Operation(summary = "处理预警事件", description = "将指定预警事件标记为已处理")
    public CommonResult<?> handleEvent(@PathVariable Long id) {
        healthEventMapper.markHandled(id);
        return CommonResult.success("事件已标记为已处理");
    }
}
