package com.antigravity.diabetes.interceptor;

import com.antigravity.diabetes.util.JwtUtil;
import com.antigravity.diabetes.vo.CommonResult;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求和登录接口
        if ("OPTIONS".equals(request.getMethod())) return true;
        if (request.getRequestURI().contains("/api/v1/auth/")) return true;

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            if (jwt != null) {
                // 将用户信息存入 Request，供后续接口使用
                request.setAttribute("userId", jwt.getClaim("userId").asLong());
                request.setAttribute("roleType", jwt.getClaim("roleType").asInt());
                return true;
            }
        }

        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"Token 无效或已过期，请重新登录\",\"data\":null}");
        return false;
    }
}
