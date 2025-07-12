package com.capstone.norush2025.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
/**
 *  카카오에서 받아온 raw 데이터 파싱
 * */
@Slf4j
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String providerId;
    private final String email;
    private final String nickname;
    private final String profileImage;
    private final AuthProvider provider;

    public CustomOAuth2User(OAuth2User oAuth2User, String registrationId, String providerIdAttrName) {
        this.oAuth2User = oAuth2User;
        this.provider = AuthProvider.valueOf(registrationId.toUpperCase()); // registrationId: 플랫폼 이름
        this.providerId = String.valueOf(oAuth2User.getAttribute(providerIdAttrName)); // providerIdAttrName: "id", "sub" 등 소셜에서 제공하는 id 필드 이름

        if (provider == AuthProvider.KAKAO) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            Map<String, Object> props = oAuth2User.getAttribute("properties");

            this.email = kakaoAccount.get("email") != null
                    ? (String) kakaoAccount.get("email")
                    : "kakao_" + this.providerId + "@noemail.com";  // 또는 null-safe 처리

            this.nickname = props.get("nickname") != null
                    ? (String) props.get("nickname")
                    : "사용자" + this.providerId;

            this.profileImage = props.get("profile_image") != null
                    ? (String) props.get("profile_image")
                    : null;

        } else if (provider == AuthProvider.GOOGLE) {
            this.email = oAuth2User.getAttribute("email");
            this.nickname = oAuth2User.getAttribute("name");
            this.profileImage = oAuth2User.getAttribute("picture");
        } else {
            this.email = null;
            this.nickname = null;
            this.profileImage = null;
        }

        log.info("✅ provider: {}, providerId: {}", provider, providerId);
        log.info("✅ email: {}, nickname: {}, profileImage: {}", email, nickname, profileImage);
    }

    public String getProviderId() { return providerId; } // 소셜 사용자 고유 ID
    public String getEmail() { return email; }

    public String getNickname() {
        return nickname;
    } // 실제 소셜 닉네임

    public String getProfileImage() { return profileImage; }
    public AuthProvider getProvider() { return provider; }

    @Override
    public Map<String, Object> getAttributes() { return oAuth2User.getAttributes(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() { return providerId; }  // OAuth2User 인터페이스 요구사항 (고유 식별자)



}

