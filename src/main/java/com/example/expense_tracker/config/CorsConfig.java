package com.example.expense_tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 允许前端开发服务器的域名
                .allowedOrigins(allowedOrigins)
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization")
                // 允许发送身份凭证（cookie等）
                .allowCredentials(true)
                // 预检请求的有效期，单位为秒
                .maxAge(3600);
    }
}