package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.dto.FollowupPlanDTO;
import com.antigravity.diabetes.entity.BizFollowupPlan;
import com.antigravity.diabetes.entity.BizFollowupTemplate;
import com.antigravity.diabetes.entity.BizHealthArticle;
import com.antigravity.diabetes.service.FollowupPlanService;
import com.antigravity.diabetes.mapper.FollowupTemplateMapper;
import com.antigravity.diabetes.mapper.HealthArticleMapper;
import com.antigravity.diabetes.mapper.SysUserMapper;
import com.antigravity.diabetes.entity.SysUser;
import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.vo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor/followup-plan")
@Tag(name = "随访计划管理", description = "医生制定和管理患者随访计划")
public class FollowupPlanController {

    @Autowired
    private FollowupPlanService followupPlanService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FollowupTemplateMapper followupTemplateMapper;

    @Autowired
    private HealthArticleMapper healthArticleMapper;

    @Autowired
    private com.antigravity.diabetes.mapper.FollowupPlanMapper followupPlanMapper;

    @GetMapping("/templates")
    @Operation(summary = "获取随访模板", description = "获取全部可用的随访模板列表")
    public CommonResult<List<BizFollowupTemplate>> getTemplates() {
        return CommonResult.success(followupTemplateMapper.findAll());
    }

    @GetMapping("/patients")
    @Operation(summary = "获取患者列表", description = "获取系统中所有患者用户")
    public CommonResult<List<SysUser>> getPatients() {
        // role_type = 4 表示患者
        List<SysUser> patients = sysUserMapper.selectByRoleType(4);
        return CommonResult.success(patients);
    }

    @PostMapping("/create")
    @LogOperation("医生制定新的随访计划")
    @Operation(summary = "创建随访计划", description = "医生为指定患者制定随访计划")
    public CommonResult<?> createPlan(@RequestBody FollowupPlanDTO dto, HttpServletRequest request) {
        Long doctorId = (Long) request.getAttribute("userId");
        if (doctorId == null) {
            return CommonResult.error("未获取到医生信息，请重新登录");
        }
        followupPlanService.createPlan(dto, doctorId);
        return CommonResult.success("随访计划创建成功");
    }

    @GetMapping("/list")
    @Operation(summary = "随访计划列表", description = "获取当前医生下发的全部随访计划")
    public CommonResult<List<BizFollowupPlan>> getDoctorPlans(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer roleType = (Integer) request.getAttribute("roleType");
        List<BizFollowupPlan> plans;
        if (roleType != null && roleType == 2) {
            plans = followupPlanService.getPlansByDoctor(userId);
        } else {
            plans = followupPlanMapper.selectAll();
        }
        return CommonResult.success(plans);
    }
    
    @PutMapping("/{id}/status/{status}")
    @LogOperation("更新随访计划执行状态")
    @Operation(summary = "更新计划状态", description = "更新随访计划执行状态：0-待执行, 1-已完成, 2-已取消")
    public CommonResult<?> updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        followupPlanService.updatePlanStatus(id, status);
        return CommonResult.success("状态更新成功");
    }

    @GetMapping("/articles")
    @Operation(summary = "健康教育文章", description = "获取健康科普文章列表")
    public CommonResult<List<BizHealthArticle>> getArticles() {
        return CommonResult.success(healthArticleMapper.findAll());
    }
}
