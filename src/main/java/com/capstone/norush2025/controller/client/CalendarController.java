package com.capstone.norush2025.controller.client;

import com.capstone.norush2025.code.SuccessCode;
import com.capstone.norush2025.dto.request.CalendarAddRequest;
import com.capstone.norush2025.dto.request.CalendarUpdateRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendars")
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

    @Operation(summary = "캘린더 일정 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<CalendarResponse.CalendarInfo>>> getCalendarEntries(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername(); //userId
        List<CalendarResponse.CalendarInfo> calendarInfo = calendarService.getCalendarEntries(userId, year, month);
        SuccessResponse<List<CalendarResponse.CalendarInfo>> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, calendarInfo);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "캘린더 일정 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 캘린더가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{calendarId}")
    public ResponseEntity<SuccessResponse<CalendarResponse.CalendarInfo>> getCalendarEntryByCalendarIdAndUserId(
            @PathVariable String calendarId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CalendarResponse.CalendarInfo calendarInfo = calendarService.getCalendarEntryByCalendarIdAndUserId(calendarId, userId);
        SuccessResponse<CalendarResponse.CalendarInfo> response = SuccessResponse.of(SuccessCode.SELECT_SUCCESS, calendarInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "캘린더 일정 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{calendarId}")
    public ResponseEntity<SuccessResponse<CalendarResponse.CalendarInfo>> updateCalendarEntry(
            @PathVariable String calendarId,
            @Valid @RequestBody CalendarUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        CalendarResponse.CalendarInfo updatedCalendar = calendarService.updateCalendarEntry(calendarId, userId, request);
        SuccessResponse<CalendarResponse.CalendarInfo> response = SuccessResponse.of(SuccessCode.UPDATE_SUCCESS, updatedCalendar);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "캘린더 일정 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<SuccessResponse<Void>> deleteCalendarEntry(
            @PathVariable String calendarId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        calendarService.deleteCalendarEntry(calendarId, userId);
        SuccessResponse<Void> response = SuccessResponse.of(SuccessCode.DELETE_SUCCESS);
        return ResponseEntity.ok(response);
    }



}
