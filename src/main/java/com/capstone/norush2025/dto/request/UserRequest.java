package com.capstone.norush2025.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Valid
@Schema(description = "회원가입 요청 DTO")
public class UserRequest {
    @Getter
    @Setter
    @Schema(description = "인증 번호 전송을 위한 이메일 요청")
    public static class EmailRequest {
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Email
        @Schema(description = "이메일", example = "abc@gmail.com")
        private String email;
    }

    @Getter
    @Setter
    public static class EmailAuth {
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Email
        @Schema(description = "이메일", example = "abc@gmail.com")
        private String email;
        @NotEmpty(message = "인증번호를 입력해주세요.")
        @Schema(description = "인증번호", example = "010716")
        private String authCode;


    }

    /**
     * 회원가입시 클라이언트가 입력하는 항목
     * */
    @Getter
    @Setter
    public static class SignUp {
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Email
        @Schema(description = "이메일", example = "abc@gmail.com")
        private String email;

//        @NotEmpty(message = "인증번호를 입력해주세요.")
//        @Schema(description = "인증번호", example = "010716")
//        private String authCode;

        @NotEmpty(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "영문자,숫자를 조합하여 8글자 이상 입력해주세요.")
        @Schema(description = "비밀번호",example = "drmdrm1234")
        private String password;

        @NotEmpty(message = "전화번호를 입력해주세요.")
        @Schema(description = "전화번호")
        private String phoneNumber;

        @NotEmpty(message = "이름을 입력해주세요.")
        @Schema(description = "이름")
        private String name;

    }

    @Getter
    @Setter
    public static class SignIn {
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Schema(description = "이메일", example = "abc@gmail.com")
        private String email;
        @NotEmpty(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호",example = "drmdrm1234")
        private String password;
    }

    @Getter
    @Setter
    public static class Reissue {
        @NotEmpty(message = "accessToken 을 입력해주세요.")
        private String accessToken;

        @NotEmpty(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }
    @Getter
    @Setter
    public static class Password {
        @NotEmpty(message = "기존 비밀번호를 입력해주세요")
        private String password;
    }
    @Getter
    @Setter
    public static class NewPassword {
        @NotEmpty(message = "기존 비밀번호를 입력해주세요")
        private String password;
        @NotEmpty(message = "새 비밀번호를 입력해주세요")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "영문자,숫자를 조합하여 8글자 이상 입력해주세요.")
        private String updatePassword;
    }



}