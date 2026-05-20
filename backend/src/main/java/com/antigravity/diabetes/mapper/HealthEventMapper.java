package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizHealthEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface HealthEventMapper {
    
    @Insert("INSERT INTO biz_health_event (patient_id, event_time, event_type, alert_level, description, is_handled) " +
            "VALUES (#{patientId}, #{eventTime}, #{eventType}, #{alertLevel}, #{description}, #{isHandled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizHealthEvent healthEvent);

    @org.apache.ibatis.annotations.Select("SELECT id, patient_id as patientId, event_time as eventTime, " +
            "event_type as eventType, alert_level as alertLevel, description " +
            "FROM biz_health_event WHERE patient_id = #{patientId} ORDER BY event_time DESC")
    java.util.List<BizHealthEvent> selectByPatientId(Long patientId);

    @org.apache.ibatis.annotations.Select("SELECT id, patient_id as patientId, event_time as eventTime, " +
            "event_type as eventType, alert_level as alertLevel, description, is_handled as isHandled " +
            "FROM biz_health_event WHERE alert_level > 0 ORDER BY is_handled ASC, event_time DESC LIMIT 20")
    java.util.List<BizHealthEvent> selectRecentAlerts();

    @org.apache.ibatis.annotations.Update("UPDATE biz_health_event SET is_handled = 1 WHERE id = #{id}")
    int markHandled(Long id);

    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM biz_health_event WHERE alert_level > 0 AND is_handled = 0")
    int countUnhandled();

    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM biz_health_event WHERE alert_level > 0 AND is_handled = 1")
    int countHandled();
}
