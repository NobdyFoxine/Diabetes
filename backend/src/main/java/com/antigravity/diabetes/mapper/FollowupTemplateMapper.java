package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizFollowupTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowupTemplateMapper {

    @Select("SELECT * FROM biz_followup_template")
    List<BizFollowupTemplate> findAll();

    @Select("SELECT * FROM biz_followup_template WHERE id = #{id}")
    BizFollowupTemplate selectById(Long id);
}
