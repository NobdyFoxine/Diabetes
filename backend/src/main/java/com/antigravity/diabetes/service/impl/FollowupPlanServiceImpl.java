package com.antigravity.diabetes.service.impl;

import com.antigravity.diabetes.dto.FollowupPlanDTO;
import com.antigravity.diabetes.entity.BizFollowupPlan;
import com.antigravity.diabetes.mapper.FollowupPlanMapper;
import com.antigravity.diabetes.service.FollowupPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowupPlanServiceImpl implements FollowupPlanService {

    @Autowired
    private FollowupPlanMapper followupPlanMapper;

    @Override
    public void createPlan(FollowupPlanDTO dto, Long doctorId) {
        BizFollowupPlan plan = new BizFollowupPlan();
        plan.setPatientId(dto.getPatientId());
        plan.setDoctorId(doctorId);
        plan.setPlanDate(dto.getPlanDate());
        plan.setContent(dto.getContent());
        plan.setStatus(0); // 默认待执行
        plan.setCreateTime(LocalDateTime.now());
        followupPlanMapper.insert(plan);
    }

    @Override
    public List<BizFollowupPlan> getPlansByDoctor(Long doctorId) {
        return followupPlanMapper.selectByDoctorId(doctorId);
    }

    @Override
    public List<BizFollowupPlan> getPlansByPatient(Long patientId) {
        return followupPlanMapper.selectByPatientId(patientId);
    }

    @Override
    public void updatePlanStatus(Long planId, Integer status) {
        followupPlanMapper.updateStatus(planId, status);
    }
}
