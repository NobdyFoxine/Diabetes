package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.mapper.SysLogMapper;
import com.antigravity.diabetes.vo.CommonResult;
import com.antigravity.diabetes.vo.SysLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/log")
@Tag(name = "系统审计日志", description = "查看系统操作审计日志")
public class SysLogController {

    @Autowired
    private SysLogMapper sysLogMapper;

    @GetMapping("/recent")
    @Operation(summary = "最近操作日志", description = "获取系统最近100条操作审计记录")
    public CommonResult<List<SysLogVO>> getRecentLogs(HttpServletRequest request) {
        List<SysLogVO> logs = sysLogMapper.selectRecentLogs();
        return CommonResult.success(logs);
    }
}
