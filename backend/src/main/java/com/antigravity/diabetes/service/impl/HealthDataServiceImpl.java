package com.antigravity.diabetes.service.impl;

import com.antigravity.diabetes.dto.HealthDataDTO;
import com.antigravity.diabetes.entity.BizHealthData;
import com.antigravity.diabetes.event.HealthDataEvent;
import com.antigravity.diabetes.mapper.HealthDataMapper;
import com.antigravity.diabetes.service.HealthDataService;
import com.antigravity.diabetes.vo.HealthDataUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class HealthDataServiceImpl implements HealthDataService {

    @Autowired
    private HealthDataMapper healthDataMapper;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthDataUploadVO uploadHealthData(HealthDataDTO dto) {
        BizHealthData healthData = new BizHealthData();
        BeanUtils.copyProperties(dto, healthData);
        healthData.setCreateTime(LocalDateTime.now());
        if (healthData.getRecordTime() == null) {
            healthData.setRecordTime(LocalDateTime.now());
        }
        
        healthDataMapper.insert(healthData);
        
        eventPublisher.publishEvent(new HealthDataEvent(this, healthData));

        HealthDataUploadVO vo = new HealthDataUploadVO();
        vo.setRecordId(healthData.getId());
        return vo;
    }

    @Override
    public List<BizHealthData> getHistory(Long patientId) {
        return healthDataMapper.selectByPatientId(patientId);
    }
}
