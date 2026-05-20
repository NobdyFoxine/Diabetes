package com.antigravity.diabetes.service.impl;

import com.antigravity.diabetes.entity.BizHealthEvent;
import com.antigravity.diabetes.mapper.HealthEventMapper;
import com.antigravity.diabetes.service.HealthEventService;
import com.antigravity.diabetes.vo.HealthEventVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthEventServiceImpl implements HealthEventService {

    @Autowired
    private HealthEventMapper healthEventMapper;

    @Override
    public List<HealthEventVO> getEventsByPatientId(Long patientId) {
        List<BizHealthEvent> events = healthEventMapper.selectByPatientId(patientId);
        return events.stream().map(event -> {
            HealthEventVO vo = new HealthEventVO();
            BeanUtils.copyProperties(event, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
