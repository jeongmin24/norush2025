package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.MemoAddRequest;
import com.capstone.norush2025.dto.request.MemoUpdateRequest;
import com.capstone.norush2025.dto.response.FavoriteRouteResponse;
import com.capstone.norush2025.dto.response.MemoResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.response.SuccessResponse;
import com.capstone.norush2025.service.MemoService;
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
@RequestMapping("/api/v1/memos")
@RequiredArgsConstructor
@Tag(name = "Memo", description = "메모 관련 API")
public class MemoController {

    private final MemoService memoService;

    @Operation(summary = "메모 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<MemoResponse.MemoInfo>> addMemo (
            @Valid @RequestBody MemoAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        String userId = userDetails.getUsername();
        MemoResponse.MemoInfo memoInfo = memoService.addMemo(userId, request);
        SuccessResponse<MemoResponse.MemoInfo> response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, memoInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "메모 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 메모가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<MemoResponse.MemoInfo>>> getMemos (
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        List<MemoResponse.MemoInfo> memoInfos = memoService.getMemos(userId);
        SuccessResponse<List<MemoResponse.MemoInfo>> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, memoInfos);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "메모 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 메모가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{memoId}")
    public ResponseEntity<SuccessResponse<MemoResponse.MemoInfo>> getMemoByUserId (
            @PathVariable String memoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        MemoResponse.MemoInfo memoInfo = memoService.getMemoByUserId(memoId, userId);
        SuccessResponse<MemoResponse.MemoInfo> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, memoInfo);
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "메모 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 메모가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{memoId}")
    public ResponseEntity<SuccessResponse<MemoResponse.MemoInfo>> updateMemo(
            @PathVariable String memoId,
            @RequestBody MemoUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails

            ) {
        String userId = userDetails.getUsername();
        MemoResponse.MemoInfo updatedInfo = memoService.updateMemo(memoId, userId, request);
        SuccessResponse<MemoResponse.MemoInfo> response = SuccessResponse.of(SuccessCode.UPDATE_SUCCESS, updatedInfo);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "메모 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 메모가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{memoId}")
    public ResponseEntity<SuccessResponse<Void>> deleteMemo(
            @PathVariable String memoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        memoService.deleteMemo(memoId, userId);
        SuccessResponse<Void> response = SuccessResponse.of(SuccessCode.DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }




}
