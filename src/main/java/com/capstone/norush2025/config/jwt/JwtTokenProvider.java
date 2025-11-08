package com.capstone.norush2025.config.jwt;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.domain.user.CustomUserInfoDto;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.response.UserResponse;
import com.capstone.norush2025.exception.TokenException;
import com.capstone.norush2025.repository.UserRepository;
import com.capstone.norush2025.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID = "userId";
    private static final String AUTHORITY = "authority";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 12 * 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    private final Key key;
    @Autowired
    private UserRepository userRepository;

    public JwtTokenProvider(){
        byte[] keyBytes = Decoders.BASE64.decode("VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHN");
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * email + password → Spring Security 인증 성공
     * → Authentication 객체 생성됨 (username = userId로 설정되어 있음)
     * → generateToken(authentication) 호출
     * → DB에서 userId로 User 조회
     * → JWT 발급 (userId + 권한 등 클레임 포함)
     * */
    public UserResponse.TokenInfo generateToken(Authentication authentication) {

        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        // 로그인 성공 후 JWT 토큰 생성시 userId 넣음
        // 추후 인증 필터에서 userRepository,findByuserId(userId)로 사용자 조회
        Claims claims = Jwts.claims();
        claims.put(USER_ID, user.getUserId());
        claims.put(AUTHORITY, user.getAuthority());

        UserResponse.TokenInfo tokenInfo = generateToken(user.getUserId(),user.getAuthority(), claims);
        return tokenInfo;
    }

    /**
     * 테스트용 토큰 발급
     * */
    public UserResponse.TokenInfo generateToken(CustomUserInfoDto user) {
        Claims claims = Jwts.claims();
        claims.put(USER_ID, user.getUserId());
        claims.put(AUTHORITY, user.getAuthority());
//        log.info(claims.get("userId",Long.class).toString());
        if ("1".equals(user.getUserId()) || user.getAuthority().equals(Authority.ADMIN)) {
            // Set the access token expiration time to infinity for TEST users
            Date accessTokenExpiresIn = new Date(Long.MAX_VALUE);

            // Generate the access token
            String accessToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(accessTokenExpiresIn)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Generate the refresh token
            String refreshToken = Jwts.builder()
                    .setExpiration(accessTokenExpiresIn)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return UserResponse.TokenInfo.builder()
                    .userId(user.getUserId())
                    .grantType(BEARER_TYPE)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                    .authority(user.getAuthority())
                    .build();
        } else {
//            // Generate the access and refresh tokens normally for non-TEST users
            UserResponse.TokenInfo tokenInfo = generateToken(user.getUserId(),user.getAuthority(), claims);
            return tokenInfo;
        }
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        String userId = claims.get(USER_ID, String.class);
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("토큰에 USER_ID 클레임이 없습니다.");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

        // Authentication객체를 만들때 UserDetails(사용자정보), Authorities(권한정보)만 담음
//        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) throws SecurityException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            // 401 Unauthorized
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            // 401 Expired
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            // 400 Bad Request
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            // 400 Bad Request
            throw e;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }catch (SignatureException e){
            throw new TokenException("Invalid JWT Token");
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    /**
     * JWT 자체를 생성하는 실제 로직
     * */
    private UserResponse.TokenInfo generateToken(String userId, Authority authority, Claims claims){
        // 현재 시간과 만료 시간 계산
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime accessTokenExpiresIn = now.plusSeconds( ACCESS_TOKEN_EXPIRE_TIME);
        ZonedDateTime refreshTokenExpiresIn = now.plusSeconds( REFRESH_TOKEN_EXPIRE_TIME);

        // JWT 액세스 토큰 생성
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(accessTokenExpiresIn.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // JWT 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(refreshTokenExpiresIn.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 토큰 정보 DTO로 래핑해서 반환
        return UserResponse.toTokenInfo(BEARER_TYPE, userId, accessToken, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, authority);

    }

}
