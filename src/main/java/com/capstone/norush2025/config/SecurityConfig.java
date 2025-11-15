package com.capstone.norush2025.config;

import com.capstone.norush2025.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. API 서버용 설정
            .csrf().disable()
            .formLogin().disable() // 기본 폼 로그인 비활성화
            .httpBasic().disable() // HTTP Basic 인증 비활성화
            
            // 2. 인가(Authorization) 설정
            .authorizeHttpRequests(requests -> requests
                // 소셜 로그인 관련 경로 및 메인 페이지는 모두 허용
                .requestMatchers("/", "/oauth2/**", "/login/**").permitAll() 
                .anyRequest().authenticated() // 그 외의 모든 요청은 인증된 사용자만 접근 허용
            )
            
            // 3. OAuth 2.0 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                // [핵심] 로그인 성공 후 사용자 정보를 처리할 서비스 등록
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) 
                )
                .defaultSuccessUrl("/") // 로그인 성공 시 기본 이동 경로
            )
            
            // 4. 로그아웃 설정
            .logout(logout -> logout
                .logoutSuccessUrl("/") // 로그아웃 성공 시 메인 페이지로 이동
                .permitAll()
            );

        return http.build();
    }
}