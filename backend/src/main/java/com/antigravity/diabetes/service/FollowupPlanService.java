package com.antigravity.diabetes.service;

import com.antigravity.diabetes.dto.FollowupPlanDTO;
import com.antigravity.diabetes.entity.BizFollowupPlan;
import java.util.List;

public interface FollowupPlanService {
    void createPlan(FollowupPlanDTO dto, Long doctorId);
    List<BizFollowupPlan> getPlansByDoctor(Long doctorId);
    List<BizFollowupPlan> getPlansByPatient(Long patientId);
    void updatePlanStatus(Long planId, Integer status);
}
