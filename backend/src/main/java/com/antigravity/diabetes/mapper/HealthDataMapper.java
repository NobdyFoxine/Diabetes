package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizHealthData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthDataMapper {
    
    @Insert("INSERT INTO biz_health_data (patient_id, record_time, glucose_value, glucose_period, systolic_bp, diastolic_bp, " +
            "heart_rate, weight, exercise_steps, diet_calories, medication_name, medication_dose, medication_time, create_time) " +
            "VALUES (#{patientId}, #{recordTime}, #{glucoseValue}, #{glucosePeriod}, #{systolicBp}, #{diastolicBp}, " +
            "#{heartRate}, #{weight}, #{exerciseSteps}, #{dietCalories}, #{medicationName}, #{medicationDose}, #{medicationTime}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizHealthData healthData);

    @Select("SELECT * FROM biz_health_data WHERE patient_id = #{patientId} ORDER BY record_time ASC LIMIT 30")
    List<BizHealthData> selectByPatientId(Long patientId);
}
