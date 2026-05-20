package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizAlertThreshold;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AlertThresholdMapper {

    @Select("SELECT * FROM biz_alert_threshold WHERE patient_id = #{patientId}")
    BizAlertThreshold selectByPatientId(Long patientId);

    @Insert("INSERT INTO biz_alert_threshold (patient_id, glucose_low, glucose_warn_low, glucose_warn_high, glucose_high, " +
            "systolic_max, diastolic_max, systolic_warn, diastolic_warn, notes) " +
            "VALUES (#{patientId}, #{glucoseLow}, #{glucoseWarnLow}, #{glucoseWarnHigh}, #{glucoseHigh}, " +
            "#{systolicMax}, #{diastolicMax}, #{systolicWarn}, #{diastolicWarn}, #{notes})")
    int insert(BizAlertThreshold threshold);

    @Update("UPDATE biz_alert_threshold SET glucose_low = #{glucoseLow}, glucose_warn_low = #{glucoseWarnLow}, " +
            "glucose_warn_high = #{glucoseWarnHigh}, glucose_high = #{glucoseHigh}, " +
            "systolic_max = #{systolicMax}, diastolic_max = #{diastolicMax}, " +
            "systolic_warn = #{systolicWarn}, diastolic_warn = #{diastolicWarn}, " +
            "notes = #{notes} WHERE patient_id = #{patientId}")
    int updateByPatientId(BizAlertThreshold threshold);
}
