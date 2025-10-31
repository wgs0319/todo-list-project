package com.ToDo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")     // 모든 경로에 적용
                .excludePathPatterns(       // 제외할 경로
                        "/",
                        "/login",
                        "/logout",
                        "/users/signup",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}
