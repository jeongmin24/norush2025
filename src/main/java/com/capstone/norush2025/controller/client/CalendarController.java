package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.CalendarAddRequest;
import com.capstone.norush2025.dto.response.CalendarResponse;
import com.capstone.norush2025.response.ErrorResponse;
import com.capstone.norush2025.response.SuccessResponse;
import com.capstone.norush2025.service.CalendarService;
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

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 관련 API")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "캘린더 일정 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<CalendarResponse.CalendarInfo>> addCalendarEntry(
            @Valid @RequestBody CalendarAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        String userId = userDetails.getUsername(); //userId
        CalendarResponse.CalendarInfo calendarInfo = calendarService.addCalendarEntry(userId, request);
        SuccessResponse<CalendarResponse.CalendarInfo> response = SuccessResponse.of(SuccessCode.INSERT_SUCCESS, calendarInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
