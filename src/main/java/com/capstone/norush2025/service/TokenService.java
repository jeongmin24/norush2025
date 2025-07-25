package com.capstone.norush2025.service;

import com.capstone.norush2025.config.jwt.JwtTokenProvider;
import com.capstone.norush2025.domain.Admin;
import com.capstone.norush2025.domain.user.CustomUserInfoDto;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.UserRequest;
import com.capstone.norush2025.dto.response.UserResponse;
import com.capstone.norush2025.exception.TokenException;
import com.capstone.norush2025.repository.AdminRepository;
import com.capstone.norush2025.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AdminRepository adminRepository;
    private static String REFRESHTOKEN = "RefreshToken:";

    public UserResponse.TokenInfo createToken(UserRequest.SignIn signIn){
        User getUser = userRepository.findByEmail(signIn.getEmail()).orElseThrow(() -> new IllegalArgumentException("이메일 정보가 없습니다"));
        if(!passwordEncoder.matches(signIn.getPassword(),getUser.getPassword())) throw new IllegalArgumentException("잘못된 비밀번호 입니다");
        CustomUserInfoDto userInfo = modelMapper.map(getUser, CustomUserInfoDto.class);
        UserResponse.TokenInfo  tokenInfo = jwtTokenProvider.generateToken(userInfo);
        tokenInfo.setUserInfo(new UserResponse.UserInfo(getUser));
        //RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisService.setValuesWithTimeUnit(REFRESHTOKEN + getUser.getUserId(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        return tokenInfo;
    }

    public UserResponse.TokenInfo createAdminToken(UserRequest.SignIn signIn){
        Admin getAdmin = adminRepository.findByEmail(signIn.getEmail()).orElseThrow(()-> new IllegalArgumentException("이메일 정보가 없습니다"));
        if(!passwordEncoder.matches(signIn.getPassword(),getAdmin.getPassword())) throw new IllegalArgumentException("잘못된 비밀번호 입니다");
        CustomUserInfoDto userInfo = CustomUserInfoDto
                .builder()
                .userId(getAdmin.getAdminId())
                .authority(getAdmin.getAuthority())
                .build();
        UserResponse.TokenInfo  tokenInfo = jwtTokenProvider.generateToken(userInfo);
        //RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisService.setValuesWithTimeUnit(REFRESHTOKEN +getAdmin.getAdminId(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(),TimeUnit.MILLISECONDS);
        return tokenInfo;
    }

    public UserResponse.TokenInfo reissue(UserRequest.Reissue reissue) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            throw new TokenException("Refresh Token 정보가 유효하지 않습니다.");
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = redisService.getRefrestToken(authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            throw new TokenException("Redis 에 RefreshToken 이 존재하지 않습니다");
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            throw new TokenException("refresh정보가 일치 하지 않습니다");
        }

        // 4. 새로운 토큰 생성
        UserResponse.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        User getUser = userRepository.findById(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
        tokenInfo.setUserInfo(new UserResponse.UserInfo(getUser));

        // 5. RefreshToken Redis 업데이트
        redisService.setValuesWithTimeUnit(REFRESHTOKEN + authentication.getName(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(),TimeUnit.MILLISECONDS);

        return tokenInfo;
    }
}