package com.capstone.norush2025.handler;

import com.capstone.norush2025.config.jwt.JwtTokenProvider;
import com.capstone.norush2025.domain.user.CustomOAuth2User;
import com.capstone.norush2025.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 소셜로그인 성공 후 로그인된 사용자 정보를 바탕으로 JWT토큰을 발급
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("✅ [SuccessHandler] 호출됨, URI: {}", request.getRequestURI());

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOAuth2User)) {
            log.error("❌ Principal 타입 오류: {}", principal.getClass().getName());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2User 타입 오류");
            return;
        }

        CustomOAuth2User oauth2User = (CustomOAuth2User) principal;
        // JWT 발급
        UserResponse.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication); // authentication.getName()이 userId 반환

        // RefreshToken 저장
        String key = "RefreshToken:" + oauth2User.getProvider() + "_" + oauth2User.getProviderId(); // provider + providerId 조합을 Redis 키로 사용
        redisTemplate.opsForValue().set(key, tokenInfo.getRefreshToken(),
                Duration.ofSeconds(tokenInfo.getRefreshTokenExpirationTime()));

        // 응답 반환
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(new ObjectMapper().writeValueAsString(tokenInfo));

    }
}

