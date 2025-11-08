package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.dto.request.RouteRequest;
import com.capstone.norush2025.dto.response.RouteResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.service.RouteService;
import com.capstone.norush2025.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
@Tag(name = "경로 조회 API", description = "Tmap을 이용한 대중교통 경로 조회 및 예측 관련 API")
public class RouteController {

    private final RouteService routeService;



    @Operation(summary = "Tmap 경로 조회 (테스트용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/test")
    public ResponseEntity<RouteResponse> testRoute(
            @RequestBody RouteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {


        String userId = userDetails.getUsername(); //userId

        System.out.println("👤 사용자: " + userDetails.getUsername());
        System.out.println("📍 받은 요청: " + request);


        // 1. 요청에서 좌표 추출
        double startX = request.getOrigin().getLng();
        double startY = request.getOrigin().getLat();
        double endX = request.getDestination().getLng();
        double endY = request.getDestination().getLat();

        System.out.printf("출발(%.4f, %.4f) → 도착(%.4f, %.4f)%n", startX, startY, endX, endY);

        // 2. 서비스 호출 (Tmap API 요청) - 인증정보 함께 넘겨줌
        RouteResponse response = routeService.findRoute(userId, startX, startY, endX, endY);

        // 3. 응답 반환
        return ResponseEntity.ok(response);
    }



}
