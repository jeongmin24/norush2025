package com.capstone.norush2025.domain.user;

import com.capstone.norush2025.code.Authority;
import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    private String userId; // 내부식별자

    /**
     * 회원가입 입력필드
     * */
    private String email; // 로그인할때 입력
    private String name;
    private String phoneNumber;
    private String password; // 로그인할때 입력

    private List<String> favoriteRouteIds = new ArrayList<>();
    private String profileImage;

    private Authority authority = Authority.USER; // 관리자 or 사용자

    private AuthProvider provider; // KAKAO, GOOGLE, LOCAL
    private String providerId; // 소셜에서 받은 유저 ID

    /**
     * 사용자 입력(email, name, phoneNumber, password) + 기본값으로 User 구성
     * */
    public static User toEntity(String email, String name, String phoneNumber, String encodedPassword) {
        User user = new User();
        user.email = email;
        user.name = name;
        user.phoneNumber = phoneNumber;
        user.password = encodedPassword;

        user.authority = Authority.USER;
        user.provider = AuthProvider.LOCAL;
        user.favoriteRouteIds = new ArrayList<>();
        user.profileImage = null;
        return user;
    }


    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }


}
