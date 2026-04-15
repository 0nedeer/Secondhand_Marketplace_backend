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
                // 移除 Bearer 前缀（如果有）
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                // 解析token获取用户ID
                Long userId = jwtUtil.validateTokenAndGetUserId(token);
                System.out.println("解析到的用户ID: " + userId);
                // 存入线程上下文，后续Controller可通过UserContext.getCurrentUserId()获取
                UserContext.setCurrentUserId(userId);
                System.out.println("UserContext 设置成功，当前用户ID: " + UserContext.getCurrentUserId());
            } catch (Exception e) {
                System.out.println("Token解析失败: " + e.getMessage());
                // token无效时不设置用户ID，让后续业务逻辑自行判断
                // 不抛出异常，避免影响不需要登录的接口
            }
        }else{
            System.out.println("没有携带token");
        }


        return true; // 始终放行，让Controller自己判断是否需要登录
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清除ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}