package com.booklog.booklogbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Vue3와 연동하기 위한 CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 프론트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // 또는 "Authorization", "Content-Type"
                .exposedHeaders("Authorization") // 응답에서 Authorization 헤더도 볼 수 있게
                .allowCredentials(true)
                .maxAge(3600);
    }
}
