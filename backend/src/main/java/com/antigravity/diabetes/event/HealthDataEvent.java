package com.antigravity.diabetes.event;

import com.antigravity.diabetes.entity.BizHealthData;
import org.springframework.context.ApplicationEvent;

public class HealthDataEvent extends ApplicationEvent {
    private final BizHealthData healthData;

    public HealthDataEvent(Object source, BizHealthData healthData) {
        super(source);
        this.healthData = healthData;
    }

    public BizHealthData getHealthData() {
        return healthData;
    }
}
