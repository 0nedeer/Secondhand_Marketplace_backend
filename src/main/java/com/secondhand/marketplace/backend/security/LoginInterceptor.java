package com.secondhand.marketplace.backend.security;

import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器 - 简易版
 * 作用：从请求头中解析token，将用户ID存入ThreadLocal
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    static {
        log.info("LoginInterceptor 类被加载");
    }

    private final JwtUtil jwtUtil;
    
    public LoginInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        log.info("LoginInterceptor 实例被创建");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("========== 拦截器开始处理请求 ==========");
        log.info("拦截器：请求URL: {}", request.getRequestURI());
        log.info("拦截器：请求方法: {}", request.getMethod());
        
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        log.info("拦截器：收到的Authorization头: {}", token);

        if (token != null && !token.isEmpty()) {
            try {
                // 移除 Bearer 前缀（如果有）
                String cleanToken = token;
                if (cleanToken.startsWith("Bearer ")) {
                    cleanToken = cleanToken.substring(7);
                    log.info("拦截器：移除Bearer前缀后: {}", cleanToken.length() > 20 ? cleanToken.substring(0, 20) + "..." : cleanToken);
                }
                // 解析token获取用户ID
                log.info("拦截器：开始解析token");
                Long userId = jwtUtil.validateTokenAndGetUserId(cleanToken);
                log.info("拦截器：Token解析成功，用户ID: {}", userId);
                // 存入线程上下文，后续Controller可通过UserContext.getCurrentUserId()获取
                UserContext.setCurrentUserId(userId);
                log.info("拦截器：UserContext 设置成功，当前用户ID: {}", UserContext.getCurrentUserId());
            } catch (Exception e) {
                log.error("拦截器：Token解析失败: {}", e.getMessage(), e);
                // token无效时不设置用户ID，让后续业务逻辑自行判断
                // 不抛出异常，避免影响不需要登录的接口
            }
        }else{
            log.info("拦截器：请求未携带token");
        }

        log.info("拦截器：处理完成，放行请求");
        return true; // 始终放行，让Controller自己判断是否需要登录
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清除ThreadLocal，防止内存泄漏
        UserContext.clear();
        log.debug("UserContext已清除");
    }
}