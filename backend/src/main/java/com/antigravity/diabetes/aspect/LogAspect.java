package com.antigravity.diabetes.aspect;

import com.antigravity.diabetes.annotation.LogOperation;
import com.antigravity.diabetes.entity.SysLog;
import com.antigravity.diabetes.mapper.SysLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Pointcut("@annotation(com.antigravity.diabetes.annotation.LogOperation)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        
        try {
            saveSysLog(point, time);
        } catch (Exception e) {
            log.error("保存系统日志异常: {}", e.getMessage());
        }
        
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        if (logOperation != null) {
            sysLog.setAction(logOperation.value());
        }

        String methodName = signature.getName();

        // 解析参数：过滤掉 HttpServletRequest 等框架类型，只保留业务参数
        Object[] args = joinPoint.getArgs();
        StringBuilder paramsBuilder = new StringBuilder();
        try {
            for (Object arg : args) {
                if (arg instanceof jakarta.servlet.http.HttpServletRequest) continue;
                if (paramsBuilder.length() > 0) paramsBuilder.append(", ");
                String str = arg != null ? arg.toString() : "null";
                // 截断单个参数过长
                if (str.length() > 300) str = str.substring(0, 300) + "...";
                paramsBuilder.append(str);
            }
        } catch (Exception e) {
            paramsBuilder.append("参数解析异常");
        }
        String params = paramsBuilder.toString();
        if (params.isEmpty()) params = "(仅含框架参数)";

        // 获取request和HTTP上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "unknown";
        String httpMethod = "?";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Long userId = (Long) request.getAttribute("userId");
            if (userId != null) {
                sysLog.setOperatorId(userId);
            }
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            httpMethod = request.getMethod();
        }

        String operationType = httpMethod;
        if ("PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            operationType = "UPDATE";
        } else if ("POST".equals(httpMethod)) {
            operationType = "CREATE";
        } else if ("DELETE".equals(httpMethod)) {
            operationType = "DELETE";
        } else if ("GET".equals(httpMethod)) {
            operationType = "QUERY";
        }

        sysLog.setTargetData(String.format("%s | %s | %s | %s",
            operationType, methodName, params, ip));
        sysLog.setCreateTime(LocalDateTime.now());

        sysLogMapper.insert(sysLog);
        log.info("【AOP审计】{} | {} | {}ms", sysLog.getAction(), operationType, time);
    }
}
