package com.antigravity.diabetes.config;

import com.antigravity.diabetes.entity.SysUser;
import com.antigravity.diabetes.mapper.SysUserMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MockDataRunner implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mapper
    public interface MockUserMapper {
        @Insert("INSERT INTO sys_user (id, username, password, real_name, role_type, create_time, is_deleted) VALUES (#{id}, #{username}, #{password}, #{realName}, #{roleType}, #{createTime}, #{isDeleted})")
        void insertMockUser(SysUser user);
    }

    @Autowired
    private MockUserMapper mockUserMapper;

    @Override
    public void run(String... args) throws Exception {
        if (sysUserMapper.selectByUsername("doctor") == null) {
            SysUser doc = new SysUser();
            doc.setId(20001L);
            doc.setUsername("doctor");
            doc.setPassword(passwordEncoder.encode("123456"));
            doc.setRealName("王医生");
            doc.setRoleType(2);
            doc.setCreateTime(LocalDateTime.now());
            doc.setIsDeleted(0);
            mockUserMapper.insertMockUser(doc);
        }
        if (sysUserMapper.selectByUsername("patient") == null) {
            SysUser pat = new SysUser();
            pat.setId(10001L);
            pat.setUsername("patient");
            pat.setPassword(passwordEncoder.encode("123456"));
            pat.setRealName("李患者");
            pat.setRoleType(4);
            pat.setCreateTime(LocalDateTime.now());
            pat.setIsDeleted(0);
            mockUserMapper.insertMockUser(pat);
        }
        if (sysUserMapper.selectByUsername("admin") == null) {
            SysUser admin = new SysUser();
            admin.setId(30001L);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRealName("系统管理员");
            admin.setRoleType(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setIsDeleted(0);
            mockUserMapper.insertMockUser(admin);
        }
        if (sysUserMapper.selectByUsername("nurse") == null) {
            SysUser nurse = new SysUser();
            nurse.setId(40001L);
            nurse.setUsername("nurse");
            nurse.setPassword(passwordEncoder.encode("123456"));
            nurse.setRealName("张护士");
            nurse.setRoleType(3);
            nurse.setCreateTime(LocalDateTime.now());
            nurse.setIsDeleted(0);
            mockUserMapper.insertMockUser(nurse);
        }
    }
}
