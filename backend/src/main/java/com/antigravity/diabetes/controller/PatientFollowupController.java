package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.entity.BizFollowupPlan;
import com.antigravity.diabetes.service.FollowupPlanService;
import com.antigravity.diabetes.vo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient/followup-plan")
@Tag(name = "患者端随访", description = "患者查看自己的随访计划")
public class PatientFollowupController {

    @Autowired
    private FollowupPlanService followupPlanService;

    @GetMapping("/list")
    @Operation(summary = "我的随访计划", description = "患者获取医生为自己制定的全部随访计划")
    public CommonResult<List<BizFollowupPlan>> getMyPlans(HttpServletRequest request) {
        Long patientId = (Long) request.getAttribute("userId");
        if (patientId == null) {
            return CommonResult.error("未获取到患者信息，请重新登录");
        }
        List<BizFollowupPlan> plans = followupPlanService.getPlansByPatient(patientId);
        return CommonResult.success(plans);
    }
}
