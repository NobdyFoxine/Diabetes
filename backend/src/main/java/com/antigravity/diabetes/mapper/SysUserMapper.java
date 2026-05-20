package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SysUserMapper {
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    SysUser selectByUsername(String username);

    @Select("SELECT id, real_name as realName, username, role_type as roleType, create_time as createTime FROM sys_user WHERE is_deleted = 0 ORDER BY id")
    List<SysUser> selectAll();

    @Select("SELECT * FROM sys_user WHERE id = #{id} AND is_deleted = 0")
    SysUser selectById(Long id);

    @Select("SELECT id, real_name as realName, username FROM sys_user WHERE role_type = #{roleType} AND is_deleted = 0")
    List<SysUser> selectByRoleType(Integer roleType);

    @org.apache.ibatis.annotations.Update("UPDATE sys_user SET role_type = #{roleType} WHERE id = #{id}")
    int updateRole(Long id, Integer roleType);

    @org.apache.ibatis.annotations.Insert("INSERT INTO sys_user (username, password, real_name, role_type, create_time, is_deleted) " +
            "VALUES (#{username}, #{password}, #{realName}, #{roleType}, #{createTime}, 0)")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);
}
