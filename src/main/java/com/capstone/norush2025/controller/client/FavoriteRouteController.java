package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.FavoriteRouteAddRequest;
import com.capstone.norush2025.dto.request.FavoriteRouteUpdateRequest;
import com.capstone.norush2025.dto.response.CalendarResponse;
import com.capstone.norush2025.dto.response.FavoriteRouteResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.response.SuccessResponse;
import com.capstone.norush2025.service.FavoriteRouteService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "즐겨찾기 관련 API")
public class FavoriteRouteController {

    private final FavoriteRouteService favoriteRouteService;

    @Operation(summary = "즐겨찾기 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo>> addFavoriteRoute(
            @Valid @RequestBody FavoriteRouteAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        String userId = userDetails.getUsername();
        FavoriteRouteResponse.FavoriteRouteInfo favoriteRouteInfo = favoriteRouteService.addFavoriteRoute(userId, request);
        SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo> response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, favoriteRouteInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "즐겨찾기 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 즐겨찾기가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<FavoriteRouteResponse.FavoriteRouteInfo>>> getFavoriteRoutes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        List<FavoriteRouteResponse.FavoriteRouteInfo> favoriteRouteInfos = favoriteRouteService.getFavoriteRoutes(userId);
        SuccessResponse<List<FavoriteRouteResponse.FavoriteRouteInfo>> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, favoriteRouteInfos);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "즐겨찾기 경로 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 즐겨찾기가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{favoriteRouteId}")
    public ResponseEntity<SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo>> getFavoriteRouteByUserId(
            @PathVariable String favoriteRouteId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        FavoriteRouteResponse.FavoriteRouteInfo favoriteRouteInfo = favoriteRouteService.getFavoriteRouteByUserId(favoriteRouteId, userId);
        SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, favoriteRouteInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "즐겨찾기 경로 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 즐겨찾기가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{favoriteRouteId}")
    public ResponseEntity<SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo>> updateFavoriteRoute(
            @PathVariable String favoriteRouteId,
            @RequestBody FavoriteRouteUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        FavoriteRouteResponse.FavoriteRouteInfo updatedInfo = favoriteRouteService.updateFavoriteRoute(favoriteRouteId, userId, request);
        SuccessResponse<FavoriteRouteResponse.FavoriteRouteInfo> response = SuccessResponse.of(SuccessCode.UPDATE_SUCCESS, updatedInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "즐겨찾기 경로 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 즐겨찾기가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{favoriteRouteId}")
    public ResponseEntity<SuccessResponse<Void>> deleteFavoriteRoute(
            @PathVariable String favoriteRouteId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        favoriteRouteService.deleteFavoriteRoute(favoriteRouteId, userId);
        SuccessResponse<Void> response = SuccessResponse.of(SuccessCode.DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }



}
