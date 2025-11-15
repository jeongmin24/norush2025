package com.capstone.norush2025.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;
    private String providerId;
    private String email;
    private String nickname;
    private String profileImage;
    private AuthProvider provider;

    public CustomOAuth2User(OAuth2User oAuth2User, String registrationId, String providerIdAttrName) {
        this.oAuth2User = oAuth2User;
        this.provider = AuthProvider.valueOf(registrationId.toUpperCase());
        
        this.providerId = String.valueOf(oAuth2User.getAttribute(providerIdAttrName)); 

        if (provider == AuthProvider.KAKAO) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            Map<String, Object> props = oAuth2User.getAttribute("properties");

            this.email = kakaoAccount.get("email") != null
                    ? (String) kakaoAccount.get("email")
                    : "kakao_" + this.providerId + "@noemail.com";

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
            
        } else if (provider == AuthProvider.NAVER) {
            Map<String, Object> response = oAuth2User.getAttribute("response");

            if (response != null && response.containsKey("id")) {
                this.providerId = (String) response.get("id");
            }

            this.email = response != null && response.get("email") != null
                    ? (String) response.get("email")
                    : "naver_" + this.providerId + "@noemail.com";

            this.nickname = response != null && response.get("name") != null
                    ? (String) response.get("name")
                    : "사용자" + this.providerId;

            this.profileImage = response != null && response.get("profile_image") != null
                    ? (String) response.get("profile_image")
                    : null;
                
        } else {
            this.email = null;
            this.nickname = null;
            this.profileImage = null;
        }

        log.info("provider: {}, providerId: {}", provider, providerId);
        log.info("email: {}, nickname: {}, profileImage: {}", email, nickname, profileImage);
    }

    public String getProviderId() { return providerId; }
    public String getEmail() { return email; }

    public String getNickname() { return nickname; }

    public String getProfileImage() { return profileImage; }
    public AuthProvider getProvider() { return provider; }

    @Override
    public Map<String, Object> getAttributes() { return oAuth2User.getAttributes(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() { return providerId; }
}

