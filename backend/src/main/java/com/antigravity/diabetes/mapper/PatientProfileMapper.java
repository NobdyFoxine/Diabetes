package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizPatientProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PatientProfileMapper {

    @Select("SELECT * FROM biz_patient_profile WHERE user_id = #{userId}")
    BizPatientProfile selectByUserId(Long userId);

    @Insert("INSERT INTO biz_patient_profile (user_id, id_card, birth_date, phone, diabetes_type, complications, " +
            "fpg, two_hpg, ga, hba1c, cholesterol, triglyceride, ldl, hdl, creatinine, urinary_microalbumin, height) " +
            "VALUES (#{userId}, #{idCard}, #{birthDate}, #{phone}, #{diabetesType}, #{complications}, " +
            "#{fpg}, #{twoHpg}, #{ga}, #{hba1c}, #{cholesterol}, #{triglyceride}, #{ldl}, #{hdl}, #{creatinine}, #{urinaryMicroalbumin}, #{height})")
    int insert(BizPatientProfile profile);

    @Update("UPDATE biz_patient_profile SET id_card = #{idCard}, birth_date = #{birthDate}, phone = #{phone}, " +
            "diabetes_type = #{diabetesType}, complications = #{complications}, fpg = #{fpg}, two_hpg = #{twoHpg}, " +
            "ga = #{ga}, hba1c = #{hba1c}, cholesterol = #{cholesterol}, triglyceride = #{triglyceride}, " +
            "ldl = #{ldl}, hdl = #{hdl}, creatinine = #{creatinine}, urinary_microalbumin = #{urinaryMicroalbumin}, " +
            "height = #{height} " +
            "WHERE user_id = #{userId}")
    int updateByUserId(BizPatientProfile profile);
}
