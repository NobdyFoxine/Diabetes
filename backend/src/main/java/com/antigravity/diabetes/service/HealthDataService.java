package com.antigravity.diabetes.service;

import com.antigravity.diabetes.dto.HealthDataDTO;
import com.antigravity.diabetes.vo.HealthDataUploadVO;
import com.antigravity.diabetes.entity.BizHealthData;
import java.util.List;

public interface HealthDataService {
    HealthDataUploadVO uploadHealthData(HealthDataDTO dto);
    List<BizHealthData> getHistory(Long patientId);
}
