package com.antigravity.diabetes.listener;

import com.antigravity.diabetes.entity.BizAlertThreshold;
import com.antigravity.diabetes.entity.BizHealthData;
import com.antigravity.diabetes.entity.BizHealthEvent;
import com.antigravity.diabetes.event.HealthDataEvent;
import com.antigravity.diabetes.mapper.AlertThresholdMapper;
import com.antigravity.diabetes.mapper.HealthEventMapper;
import com.antigravity.diabetes.websocket.DoctorWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
public class AlertEngineListener {

    @Autowired
    private HealthEventMapper healthEventMapper;

    @Autowired
    private AlertThresholdMapper alertThresholdMapper;

    // 全局默认阈值
    private static final BigDecimal DEF_GLUCOSE_LOW = new BigDecimal("3.9");
    private static final BigDecimal DEF_GLUCOSE_WARN_LOW = new BigDecimal("4.5");
    private static final BigDecimal DEF_GLUCOSE_WARN_HIGH = new BigDecimal("10.0");
    private static final BigDecimal DEF_GLUCOSE_HIGH = new BigDecimal("16.7");
    private static final int DEF_SYSTOLIC_MAX = 140;
    private static final int DEF_DIASTOLIC_MAX = 90;
    private static final int DEF_SYSTOLIC_WARN = 130;
    private static final int DEF_DIASTOLIC_WARN = 85;

    @EventListener
    public void processHealthDataAlert(HealthDataEvent event) {
        BizHealthData data = event.getHealthData();

        // 读取患者个性化阈值，无配置则用默认值
        BizAlertThreshold threshold = alertThresholdMapper.selectByPatientId(data.getPatientId());

        BigDecimal glucoseLow = getOrDefault(threshold != null ? threshold.getGlucoseLow() : null, DEF_GLUCOSE_LOW);
        BigDecimal glucoseWarnLow = getOrDefault(threshold != null ? threshold.getGlucoseWarnLow() : null, DEF_GLUCOSE_WARN_LOW);
        BigDecimal glucoseWarnHigh = getOrDefault(threshold != null ? threshold.getGlucoseWarnHigh() : null, DEF_GLUCOSE_WARN_HIGH);
        BigDecimal glucoseHigh = getOrDefault(threshold != null ? threshold.getGlucoseHigh() : null, DEF_GLUCOSE_HIGH);
        int systolicMax = nvl(threshold != null ? threshold.getSystolicMax() : null, DEF_SYSTOLIC_MAX);
        int diastolicMax = nvl(threshold != null ? threshold.getDiastolicMax() : null, DEF_DIASTOLIC_MAX);
        int systolicWarn = nvl(threshold != null ? threshold.getSystolicWarn() : null, DEF_SYSTOLIC_WARN);
        int diastolicWarn = nvl(threshold != null ? threshold.getDiastolicWarn() : null, DEF_DIASTOLIC_WARN);

        Integer alertLevel = 0;
        String alertMsg = "";
        String recommendArticle = null;

        if (data.getGlucoseValue() != null) {
            BigDecimal glucose = data.getGlucoseValue();
            if (glucose.compareTo(glucoseHigh) > 0) {
                alertLevel = 2;
                alertMsg += "血糖异常偏高(" + glucose + " mmol/L)！";
                recommendArticle = "高血糖应急处理指南";
            } else if (glucose.compareTo(glucoseLow) < 0) {
                alertLevel = 2;
                alertMsg += "血糖异常偏低(" + glucose + " mmol/L)！";
                recommendArticle = "低血糖急救与预防";
            } else if (glucose.compareTo(glucoseWarnHigh) > 0) {
                if (alertLevel < 1) alertLevel = 1;
                alertMsg += "血糖出现波动偏高(" + glucose + " mmol/L)。";
                recommendArticle = "血糖波动管理策略";
            } else if (glucose.compareTo(glucoseWarnLow) < 0) {
                if (alertLevel < 1) alertLevel = 1;
                alertMsg += "血糖偏低需关注(" + glucose + " mmol/L)。";
                recommendArticle = "低血糖预防与饮食调整";
            }
        }

        if (data.getSystolicBp() != null && data.getDiastolicBp() != null) {
            Integer sys = data.getSystolicBp();
            Integer dia = data.getDiastolicBp();
            if (sys >= systolicMax || dia >= diastolicMax) {
                alertLevel = 2;
                alertMsg += "血压异常偏高(" + sys + "/" + dia + " mmHg)！";
                recommendArticle = "高血压急症应对方案";
            } else if (sys >= systolicWarn || dia >= diastolicWarn) {
                if (alertLevel < 1) alertLevel = 1;
                alertMsg += "血压偏高(" + sys + "/" + dia + " mmHg)。";
                recommendArticle = "血压日常监测与生活干预";
            }
        }

        if (alertLevel > 0 && !alertMsg.isEmpty()) {
            alertMsg = "触发" + (alertLevel == 2 ? "红色危急" : "橙色关注") + "预警：" + alertMsg;
            if (recommendArticle != null && alertLevel == 1) {
                alertMsg += " 建议推送：【" + recommendArticle + "】";
            }
        }

        if (alertLevel > 0) {
            BizHealthEvent healthEvent = new BizHealthEvent();
            healthEvent.setPatientId(data.getPatientId());
            healthEvent.setEventTime(data.getRecordTime() != null ? data.getRecordTime() : LocalDateTime.now());
            healthEvent.setEventType(2);
            healthEvent.setAlertLevel(alertLevel);
            healthEvent.setDescription(alertMsg);
            healthEvent.setIsHandled(0);

            healthEventMapper.insert(healthEvent);

            log.warn("【预警流转引擎】患者[{}]触发{}级预警: {}", data.getPatientId(), alertLevel, alertMsg);

            try {
                Long mockDoctorId = 20001L;
                String wsMsg = "{\"patientId\":" + data.getPatientId()
                        + ",\"alertLevel\":" + alertLevel
                        + ",\"msg\":\"" + alertMsg + "\"}";
                DoctorWebSocketServer.sendAlertMessage(mockDoctorId, wsMsg);
            } catch (Exception e) {
                log.error("【WebSocket推送】推送失败", e);
            }
        } else {
            BizHealthEvent normalEvent = new BizHealthEvent();
            normalEvent.setPatientId(data.getPatientId());
            normalEvent.setEventTime(data.getRecordTime() != null ? data.getRecordTime() : LocalDateTime.now());
            normalEvent.setEventType(4);
            normalEvent.setAlertLevel(0);
            String desc = "患者完成日常体征上报，";
            if (data.getGlucoseValue() != null) desc += "血糖 " + data.getGlucoseValue() + " mmol/L。";
            if (data.getSystolicBp() != null && data.getDiastolicBp() != null)
                desc += "血压 " + data.getSystolicBp() + "/" + data.getDiastolicBp() + " mmHg。";
            desc += "指标均在正常范围内。";
            normalEvent.setDescription(desc);
            healthEventMapper.insert(normalEvent);
        }
    }

    private static BigDecimal getOrDefault(BigDecimal val, BigDecimal def) {
        return val != null ? val : def;
    }

    private static int nvl(Integer val, int def) {
        return val != null ? val : def;
    }
}

