package com.capstone.norush2025.service;

import com.capstone.norush2025.common.FileDto;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.response.UserResponse;
import com.capstone.norush2025.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void checkDuplicatedEmail(String email)  {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.debug("UserService.checkDuplicatedEmail exception occur email: {}", email);
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다");
        }
    }

    // 가입 전 중복확인
    public Boolean findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isEmpty();
    }

    public UserResponse.UserInfo updateProfile(User user, FileDto fileDto){
        user.updateProfileImage(fileDto.getUploadFileUrl());
        return new UserResponse.UserInfo(userRepository.save(user));
    }

    public void verifyPassword(User user,String password){
        if(!passwordEncoder.matches(password,user.getPassword())) throw new IllegalArgumentException("잘못된 비밀번호 입니다");
    }

    public void updatePassword(User user, String password, String updatePassword){
        if(!passwordEncoder.matches(password,user.getPassword())) throw new IllegalArgumentException("잘못된 비밀번호 입니다");
        user.updatePassword(passwordEncoder.encode(updatePassword));
        userRepository.save(user);
    }

    public UserResponse.UserInfo createUser(String email, String name, String phoneNumber, String password) {
        this.checkDuplicatedEmail(email);
        User newUser = User.toEntity(email, name, phoneNumber, passwordEncoder.encode(password));
        User savedUser = userRepository.save(newUser);
        return new UserResponse.UserInfo(savedUser);
    }

    public User getUser(String userId){
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
    }


    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
