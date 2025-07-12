package com.capstone.norush2025.config;

import com.capstone.norush2025.config.jwt.JwtAuthenticationFilter;
import com.capstone.norush2025.config.jwt.JwtTokenProvider;
import com.capstone.norush2025.handler.CustomAccessDenierHandler;
import com.capstone.norush2025.handler.CustomAuthenticationEntryPoint;
import com.capstone.norush2025.handler.OAuth2SuccessHandler;
import com.capstone.norush2025.service.CustomOAuth2UserService;
import com.capstone.norush2025.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // 예외 처리
    private final CustomAccessDenierHandler accessDenierHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // 소셜 로그인
    private final CustomOAuth2UserService customOAuth2UserService;  // 추가
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // 인증 없이 접근 가능
    private static final String[] AUTH_WHITELIST ={
            "/",
            "/swagger-ui/index.html",
            "/api/v2/auth/**",
            "/api/v1/auth/**",
            "/index.html",
            "/api/docs/v2/**",
            "/v3/**","/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "none/api/**",
            "/login/oauth2/**",
            "/oauth2/authorization/**"
    };

    private final RedisTemplate redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // CSRF, CORS 세션 설정
        http.csrf((csrf) -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본 로그인 방식 해제
        http.formLogin((form) -> form.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);

        // JWT 필터 추가
        http.addFilterBefore(new JwtAuthenticationFilter( redisTemplate,jwtTokenProvider,customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        // 예외 핸들러 설정
        http.exceptionHandling((exceptionHandling )
                -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint) // 로그인 안 한 사용자
                .accessDeniedHandler(accessDenierHandler) // 권한 없는 사용자
        );

        // 인가 규칙
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll() // 인증 없이 접근 가능
                .requestMatchers("/api/v2/**").hasAuthority("ADMIN") // ADMIN만 접근 가능
                .anyRequest().authenticated()); // 나머지는 로그인한 사용자만 접근 가능

        // 소셜 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("❌ [OAuth2 실패] {}", exception.getMessage(), exception);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 인증 실패");
                })
        );

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
