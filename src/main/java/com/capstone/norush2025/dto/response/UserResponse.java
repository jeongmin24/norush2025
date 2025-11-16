package com.capstone.norush2025.dto.response;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    /**
     * 회원가입 후 제공할 필드
     * */
    @Getter
    @Setter
    @Schema(description = "유저 정보")
    public static class UserInfo {
        @Schema(description = "MongoDB 사용자 ID", example = "60f73c2fc9b1b9235d74f4c2")
        private String userId;

        @Schema(description = "이메일", example = "example@gmail.com")
        private String email;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "전화번호", example = "01012345678")
        private String phoneNumber;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.png")
        private String profileImage;

        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.phoneNumber = user.getPhoneNumber();
            this.profileImage = user.getProfileImage();
        }
    }

    @Getter
    @Builder
    public static class UserUpdateResponse {
        private String name;
        private String phoneNumber;
        private String profileImage;
    }

    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @Schema(description = "토큰 정보")
    public static class TokenInfo {
        @Schema(description = "MongoDB 사용자 ID")
        private String userId;

        @Schema(description = "토큰 타입", example = "Bearer")
        private String grantType;

        @Schema(description = "엑세스 토큰")
        private String accessToken;

        @Schema(description = "리프레시 토큰")
        private String refreshToken;

        @Schema(description = "리프레시 토큰 만료 시간")
        private Long refreshTokenExpirationTime;

        @Schema(description = "권한", example = "USER")
        private Authority authority;

        private UserInfo userInfo;
    }

    public static TokenInfo toTokenInfo(String BEARER_TYPE, String userId, String accessToken, String refreshToken, Long REFRESH_TOKEN_EXPIRE_TIME, Authority authority){

        return TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .authority(authority)
                .build();
    }
}

