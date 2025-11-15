package com.capstone.norush2025.controller.admin;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.UserRequest;
import com.capstone.norush2025.dto.response.UserResponse;
import com.capstone.norush2025.response.SuccessResponse;
import com.capstone.norush2025.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("AdminAuthController")
@RequestMapping("/api/v2/auth/")
@Tag(name = "Admin-Auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;
    @Operation(summary = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<SuccessResponse<UserResponse.TokenInfo>> signIn(@Valid @RequestBody UserRequest.SignIn signIn){
        UserResponse.TokenInfo tokenInfo = tokenService.createAdminToken(signIn);
        SuccessResponse response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, tokenInfo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
