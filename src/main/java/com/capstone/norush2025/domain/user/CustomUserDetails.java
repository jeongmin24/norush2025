package com.capstone.norush2025.domain.user;

import com.capstone.norush2025.code.Authority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User와 UserDetails 연결
 * */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {
    private final CustomUserInfoDto customUserInfo;

    public Authority getAuthority(){
        return customUserInfo.getAuthority();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> authorities = new ArrayList<>();
        authorities.add(customUserInfo.getAuthority().toString());
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return customUserInfo.getPassword();
    }

    /**
     * 인증된 사용자 식별자 = userId, authentication.getName()이 userId 반환
     * */
    @Override
    public String getUsername() {
        return customUserInfo.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap(); // 실제로 필요하면 소셜 정보 map으로 전달
    }

    /**
     * 소셜로그인 고유식별자
     * */
    @Override
    public String getName() {
        return customUserInfo.getUserId(); // id
    }

}
