package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizHealthArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthArticleMapper {

    @Select("SELECT * FROM biz_health_article ORDER BY id")
    List<BizHealthArticle> findAll();

    @Select("SELECT * FROM biz_health_article WHERE id = #{id}")
    BizHealthArticle selectById(Long id);

    @Select("SELECT * FROM biz_health_article WHERE tags LIKE CONCAT('%', #{keyword}, '%') LIMIT 10")
    List<BizHealthArticle> searchByTag(String keyword);
}
