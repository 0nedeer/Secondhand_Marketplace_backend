package com.secondhand.marketplace.backend.config;

import com.secondhand.marketplace.backend.security.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:8080}")
    private String[] allowedOrigins;
    
    // 公开接口列表（不需要登录）
    private static final List<String> publicPaths = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/user/sms-login",
            "/api/user/sms/send-code",
            "/api/user/forgot-password",
            "/api/user/reset-password",
            "/api/payments/callback",
            "/api/products/public/**",      // 商品公开接口
            "/api/products/list",
            "/api/products/detail/**",
            "/api/forum/public/**",         // 论坛公开接口
            "/api/forum/post/list",
            "/api/forum/post/**",
            "/api/forum/comment/post/**",
            "/swagger-ui/**",               // Swagger文档
            "/v3/api-docs/**",              // API文档
            "/doc.html",                    // Knife4j文档
            "/swagger-resources/**",
            "/webjars/**",
            "/error",                       // 错误路径
            "/static/**",                   // 静态资源
            "/uploads/**"                    // 上传文件访问
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("========== 注册登录拦截器 ==========");
        
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(publicPaths);
        
        log.info("拦截器注册成功，公开接口数量: {}", publicPaths.size());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("========== 配置CORS跨域规则 ==========");
        log.info("配置的允许域名: {}", Arrays.toString(allowedOrigins));
        
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)  // 使用allowedOriginPatterns支持通配符
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600)
                .exposedHeaders("Authorization", "X-Total-Count", "X-Token");
        
        log.info("CORS配置完成");
    }
}