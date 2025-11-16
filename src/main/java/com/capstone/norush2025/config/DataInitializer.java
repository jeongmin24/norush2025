package com.capstone.norush2025.config;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.domain.user.AuthProvider;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.repository.UserRepository;
import com.capstone.norush2025.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initData() {
        String testEmail = "user@1234";

        if (userRepository.existsByEmail(testEmail)) {
            return;
        }

        User user = new User();
        user.setEmail(testEmail); // 로그인 시 user@1234 로 입력
        user.setName("테스트 유저");
        user.setPhoneNumber("010-0000-0000");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setAuthority(Authority.USER);
        user.setProvider(AuthProvider.LOCAL);

        userRepository.save(user);
        System.out.println("테스트 계정 생성 완료: " + testEmail + " / 1234");

    }
}
