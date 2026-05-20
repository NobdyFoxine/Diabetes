package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.SysLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import com.antigravity.diabetes.vo.SysLogVO;
import java.util.List;

@Mapper
public interface SysLogMapper {
    @Insert("INSERT INTO sys_log (operator_id, action, target_data, create_time) " +
            "VALUES (#{operatorId}, #{action}, #{targetData}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysLog sysLog);

    @Select("SELECT l.id, l.operator_id as operatorId, u.real_name as operatorName, u.role_type as roleType, " +
            "l.action, l.target_data as targetData, l.create_time as createTime " +
            "FROM sys_log l LEFT JOIN sys_user u ON l.operator_id = u.id " +
            "ORDER BY l.create_time DESC LIMIT 100")
    List<SysLogVO> selectRecentLogs();
}
