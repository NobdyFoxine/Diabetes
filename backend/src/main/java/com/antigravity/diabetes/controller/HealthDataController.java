package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.dto.HealthDataDTO;
import com.antigravity.diabetes.service.HealthDataService;
import com.antigravity.diabetes.vo.CommonResult;
import com.antigravity.diabetes.vo.HealthDataUploadVO;
import com.antigravity.diabetes.entity.BizHealthData;
import com.antigravity.diabetes.annotation.LogOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient/health-data")
@Tag(name = "体征数据采集", description = "患者日常血糖、血压等体征数据的上报与查询")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    @PostMapping("/upload")
    @LogOperation("患者上报日常体征数据")
    @Operation(summary = "上报体征数据", description = "提交血糖、血压等数据，触发预警引擎分析")
    public CommonResult<?> uploadHealthData(@RequestBody HealthDataDTO dto) {
        try {
            HealthDataUploadVO vo = healthDataService.uploadHealthData(dto);
            return CommonResult.success(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error("服务器内部错误: " + e.getMessage());
        }
    }

    @GetMapping("/history/{patientId}")
    @Operation(summary = "查询历史体征", description = "根据患者ID获取全部历史体征记录")
    public CommonResult<List<BizHealthData>> getHistory(@PathVariable("patientId") Long patientId) {
        List<BizHealthData> history = healthDataService.getHistory(patientId);
        return CommonResult.success(history);
    }
}
