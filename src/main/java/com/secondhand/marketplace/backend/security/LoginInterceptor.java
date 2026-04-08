package com.secondhand.marketplace.backend.security;

import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器 - 简易版
 * 作用：从请求头中解析token，将用户ID存入ThreadLocal
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            try {
                // 解析token获取用户ID
                Long userId = jwtUtil.validateTokenAndGetUserId(token);
                // 存入线程上下文，后续Controller可通过UserContext.getCurrentUserId()获取
                UserContext.setCurrentUserId(userId);
            } catch (Exception e) {
                // token无效时不设置用户ID，让后续业务逻辑自行判断
                // 不抛出异常，避免影响不需要登录的接口
            }
        }

        return true; // 始终放行，让Controller自己判断是否需要登录
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清除ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}