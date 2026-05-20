package com.antigravity.diabetes.service.impl;

import com.antigravity.diabetes.dto.LoginDTO;
import com.antigravity.diabetes.entity.SysUser;
import com.antigravity.diabetes.mapper.SysUserMapper;
import com.antigravity.diabetes.service.AuthService;
import com.antigravity.diabetes.util.JwtUtil;
import com.antigravity.diabetes.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleType());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setRoleType(user.getRoleType());

        return vo;
    }

    @Override
    public void register(SysUser user) {
        SysUser exist = sysUserMapper.selectByUsername(user.getUsername());
        if (exist != null) throw new RuntimeException("账号已存在");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(user);
    }
}
