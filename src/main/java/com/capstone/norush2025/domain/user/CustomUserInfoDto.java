package com.capstone.norush2025.domain.user;

import com.capstone.norush2025.code.Authority;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserInfoDto {
    private String userId;
    private String email;
    private String password;
    private Authority authority;

    public static CustomUserInfoDto from(User user) {
        return CustomUserInfoDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authority(user.getAuthority())
                .build();
    }

}
