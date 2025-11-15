package com.capstone.norush2025.service;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.domain.user.*;
import com.capstone.norush2025.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
/**
 * 소셜 로그인 성공 후 카카오/구글에서 받은 사용자 정보를 앱의 사용자(User)로 변환해서 반환
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {


        try {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request); // access token을 이용해 사용자 정보 API를 호출해서 사용자 정보 (nickname, profile_image)를 받아옴

            String registrationId = request.getClientRegistration().getRegistrationId(); // ex: kakao
            String providerIdAttrName = request.getClientRegistration().getProviderDetails()
                    .getUserInfoEndpoint().getUserNameAttributeName(); // ex: id

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User, registrationId, providerIdAttrName); // nickname, profileImage 필드를 추출해 정리

            // 로그 추가
            log.info("[OAuth2UserService] email={}, nickname={}, provider={}, providerId={}",
                    customOAuth2User.getEmail(),
                    customOAuth2User.getNickname(),
                    customOAuth2User.getProvider(),
                    customOAuth2User.getProviderId());


            // DB에 유저 저장 또는 조회
            User user = userRepository.findByProviderAndProviderId(customOAuth2User.getProvider(), customOAuth2User.getProviderId())
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email(customOAuth2User.getEmail())
                            .name(customOAuth2User.getNickname())
                            .profileImage(customOAuth2User.getProfileImage())
                            .provider(customOAuth2User.getProvider())
                            .providerId(customOAuth2User.getProviderId())
                            .authority(Authority.USER)
                            .build()));

            // CustomUserDetails로 변환하여 반환
            return customOAuth2User
        } catch (Exception e) {
            log.error("OAuth2 로그인 실패: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_error", "OAuth2 로그인 중 오류 발생", null), e);
        }

    }
}
