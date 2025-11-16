package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.common.Coordinate;
import com.capstone.norush2025.dto.request.RouteRequest;
import com.capstone.norush2025.dto.request.RouteStationRequest;
import com.capstone.norush2025.dto.request.SaveRouteRequest;
import com.capstone.norush2025.dto.response.ODsayRouteResponse;
import com.capstone.norush2025.dto.response.RouteResponse;
import com.capstone.norush2025.dto.response.TmapRouteResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.service.RouteService;
import com.capstone.norush2025.service.StationCoordinateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/route")
@RequiredArgsConstructor
@Tag(name = "ROUTE", description = "대중교통 api를 이용한 대중교통 경로 조회 및 예측 API")
public class RouteController {

    private final RouteService routeService;
    private final StationCoordinateService stationCoordinateService;

    @Value("${odsay.api.key}")
    private String apiKey;

    /**
     * tmap으로 대중교통 경로 요청
     * 1. tmap 서버로 요청
     * 2. tmap 서버에서 응답 받기
     * 3. 예측 서버로 요청
     * 4. 스코어 계산
     * 5. 경로 저장
     * 6. 경로 응답
     * 7. 응답시 SuccessResponse로 감싸기
     */


    @Operation(summary = "Tmap 경로 조회 (테스트용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/tmap/test")
    public ResponseEntity<TmapRouteResponse> testRoute(
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
        TmapRouteResponse response = routeService.findRoute(userId, startX, startY, endX, endY);

        // 3. 응답 반환
        return ResponseEntity.ok(response);
    }

    // [ODsay 경로 조회]
    @Operation(summary = "ODsay 경로 조회 (후보 경로용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/odsay/test")
    public ResponseEntity<ODsayRouteResponse> testOdsayRoute(
            @RequestBody RouteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();

        System.out.println("👤 사용자: " + userId);
        System.out.println("📍 받은 요청: " + request);

        double startX = request.getOrigin().getLng();
        double startY = request.getOrigin().getLat();
        double endX = request.getDestination().getLng();
        double endY = request.getDestination().getLat();

        System.out.printf("출발(%.4f, %.4f) → 도착(%.4f, %.4f)%n", startX, startY, endX, endY);

        ODsayRouteResponse response = routeService.findRouteByOdsay(userId, startX, startY, endX, endY);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "혼잡도 경로 조회 - 출발역, 도착역 입력형식")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/predict/station")
    public ResponseEntity<RouteResponse> getPredictedRouteByStation(
            @RequestBody RouteStationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();

        // 1) 역 → 좌표 변환
        Coordinate origin = stationCoordinateService.findCoordinate(request.getFrom());
        Coordinate destination = stationCoordinateService.findCoordinate(request.getTo());

        // 2) datetime 처리 (옵션)
        String departure = request.getDatetime();
        if (departure == null || departure.isBlank()) {
            // datetime이 없으면 현재시간을 기본값으로 사용
            departure = LocalDateTime.now().toString();
        }

        // 좌표기반 경로조회 및 혼잡도 스코어 정렬
        RouteResponse response = routeService.findRouteWithCongestion(
                userId,
                origin.getLng(),
                origin.getLat(),
                destination.getLng(),
                destination.getLat()

        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "혼잡도 경로 조회 - 좌표직접요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/predict")
    public ResponseEntity<RouteResponse> getPredictedRoute(
            @RequestBody RouteRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        String userId = userDetails.getUsername();

        log.info("📍 경로 예측 요청: origin=({}, {}), dest=({}, {}), user={}",
                request.getOrigin().getLng(),
                request.getOrigin().getLat(),
                request.getDestination().getLng(),
                request.getDestination().getLat(),
                userId);

        RouteResponse response = routeService.findRouteWithCongestion(
                userId,
                request.getOrigin().getLng(), // startX (경도)
                request.getOrigin().getLat(), // startY (위도)
                request.getDestination().getLng(), // endX (경도)
                request.getDestination().getLat()  // endY (위도)
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "혼잡도 경로 저장 - addFavoriteRoute로 대체, 사용X")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "경로 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/route/save")
    public ResponseEntity<?> saveRoute(
            @RequestHeader("userId") String userId,
            @RequestBody SaveRouteRequest req
    ) {
        Long routeId = routeService.saveRoute(userId, req);
        return ResponseEntity.ok(Map.of("routeId", routeId));
    }





}
