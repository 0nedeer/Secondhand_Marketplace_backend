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

/**
 * Web配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    // 从配置文件读取允许的域名（生产环境必须配置）
    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    // 公开接口列表（不需要登录）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/user/sms-login",
            "/api/user/sms/send-code",
            "/api/user/forgot-password",
            "/api/user/reset-password",
            "/api/payments/callback",
            "/api/products/public/**",      // 商品公开接口
            "/api/forum/public/**",         // 论坛公开接口
            "/swagger-ui/**",               // Swagger文档
            "/v3/api-docs/**",              // API文档
            "/doc.html"                     // Knife4j文档
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("========== 注册登录拦截器 ==========");
        
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")                    // 拦截所有API请求
                .excludePathPatterns(PUBLIC_PATHS)             // 排除公开接口
                .excludePathPatterns("/error")                 // 排除错误路径
                .excludePathPatterns("/static/**")             // 排除静态资源
                .excludePathPatterns("/uploads/**");           // 排除上传文件访问
        
        log.info("拦截器注册成功，拦截路径: /api/**");
        log.info("公开接口数量: {}", PUBLIC_PATHS.size());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("配置CORS跨域规则");
        
        registry.addMapping("/**")
                // 生产环境必须指定具体域名，开发环境可以用通配符
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600)
                .exposedHeaders("Authorization", "X-Total-Count");
        
        log.info("CORS配置完成，允许的域名: {}", Arrays.toString(allowedOrigins));
    }
}