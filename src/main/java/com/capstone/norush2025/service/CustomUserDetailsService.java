package com.capstone.norush2025.service;

import com.capstone.norush2025.domain.Admin;
import com.capstone.norush2025.domain.user.CustomUserDetails;
import com.capstone.norush2025.domain.user.CustomUserInfoDto;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.repository.AdminRepository;
import com.capstone.norush2025.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Spring Security에서 사용자 인증시 사용자가 존재하는지 확인
 * */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;


    /**
     * JWT 인증시 JWT에서 꺼낸 userId를 인자로 함 -> 내부 식별자로 유저 조회
     * */
    public UserDetails loadUserByUsername(String userId) {
        // 관리자 우선 조회
        Optional<Admin> adminOpt = adminRepository.findByAdminId(userId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            CustomUserInfoDto userInfoDto = CustomUserInfoDto.builder()
                    .email(admin.getEmail())
                    .authority(admin.getAuthority())
                    .userId(admin.getAdminId())
                    .build();
            return new CustomUserDetails(userInfoDto);
        }

        // 일반 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        CustomUserInfoDto userInfoDto = modelMapper.map(user, CustomUserInfoDto.class);
        return new CustomUserDetails(userInfoDto);
    }




}
