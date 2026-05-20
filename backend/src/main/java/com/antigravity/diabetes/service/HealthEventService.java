package com.antigravity.diabetes.service;

import com.antigravity.diabetes.vo.HealthEventVO;
import java.util.List;

public interface HealthEventService {
    List<HealthEventVO> getEventsByPatientId(Long patientId);
}
