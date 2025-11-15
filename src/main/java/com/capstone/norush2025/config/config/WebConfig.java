package com.capstone.norush2025.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 허용
                .allowedOrigins(
                        "http://localhost:8081",                 // React Native 로컬 앱 (Metro)
                        "exp://127.0.0.1:19000",                // Expo 로컬
                        "https://norush2025-i8pt.onrender.com",
                        "https://expo.dev" // expo 웹뷰

                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);

    }
}
