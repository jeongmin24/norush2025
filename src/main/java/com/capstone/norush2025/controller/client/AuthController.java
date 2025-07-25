package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.UserRequest;
import com.capstone.norush2025.dto.response.UserResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.response.SuccessResponse;
import com.capstone.norush2025.service.TokenService;
import com.capstone.norush2025.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth",description ="token 관련")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 혹은 요청 콘텐츠가 올바르지 않을 때,",content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청한 URL/URI와 일치하는 항목을 찾지 못함,",content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "외부 API 요청 실패, 정상적 수행을 할 수 없을 때,",content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("signup")
    public ResponseEntity<SuccessResponse<UserResponse.UserInfo>> signUp(@Valid @RequestBody UserRequest.SignUp signUp){
//        userService.verifiedCode(signUp.getEmail(), signUp.getAuthCode());
        userService.checkDuplicatedEmail(signUp.getEmail());
        UserResponse.UserInfo userInfo = userService.createUser(signUp.getEmail(), signUp.getName(), signUp.getPhoneNumber(), signUp.getPassword());
        SuccessResponse response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, userInfo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "로그인")
    @PostMapping("signin")
    public ResponseEntity<SuccessResponse<UserResponse.TokenInfo>> signIn(@Valid @RequestBody UserRequest.SignIn signIn){
        UserResponse.TokenInfo tokenInfo = tokenService.createToken(signIn);
        SuccessResponse response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, tokenInfo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("reissue")
    public ResponseEntity<SuccessResponse<UserResponse.TokenInfo>> reissue(@RequestBody @Validated UserRequest.Reissue reissue){
        UserResponse.TokenInfo tokenInfo = tokenService.reissue(reissue);
        SuccessResponse response = SuccessResponse.of(SuccessCode.UPDATE_SUCCESS, tokenInfo);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
