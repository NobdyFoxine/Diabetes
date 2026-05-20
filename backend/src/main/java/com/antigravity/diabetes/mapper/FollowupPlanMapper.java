package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizFollowupPlan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FollowupPlanMapper {
    
    @Insert("INSERT INTO biz_followup_plan (patient_id, doctor_id, plan_date, content, status, create_time) " +
            "VALUES (#{patientId}, #{doctorId}, #{planDate}, #{content}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizFollowupPlan plan);

    @Select("SELECT * FROM biz_followup_plan WHERE doctor_id = #{doctorId} ORDER BY plan_date DESC")
    List<BizFollowupPlan> selectByDoctorId(Long doctorId);

    @Select("SELECT * FROM biz_followup_plan ORDER BY plan_date DESC")
    List<BizFollowupPlan> selectAll();

    @Select("SELECT * FROM biz_followup_plan WHERE patient_id = #{patientId} ORDER BY plan_date DESC")
    List<BizFollowupPlan> selectByPatientId(Long patientId);

    @Update("UPDATE biz_followup_plan SET status = #{status} WHERE id = #{id}")
    int updateStatus(Long id, Integer status);
}
