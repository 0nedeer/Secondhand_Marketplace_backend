package com.secondhand.marketplace.backend.config;

import com.secondhand.marketplace.backend.security.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置 - 简易版
 * 作用：注册拦截器，配置哪些接口需要/不需要拦截
 */
@Configuration
@RequiredArgsConstructor

public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("========== 注册拦截器 ==========");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有/api/开头的请求
                .excludePathPatterns(        // 不拦截以下接口（不需要登录就能访问）
                        "/api/user/register",
                        "/api/user/login",
                        "/api/user/sms-login",
                        "/api/user/sms/send-code",
                        "/api/user/forgot-password",
                        "/api/user/reset-password",
                        "/api/payments/callback"
                        // 商品、论坛等模块的公开接口后续再加
                );
        System.out.println("拦截器已注册，拦截路径: /api/**");
    }
}
