package com.capstone.norush2025.service;

import com.capstone.norush2025.code.ErrorCode;
import com.capstone.norush2025.domain.Calendar;
import com.capstone.norush2025.domain.user.AuthProvider;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.CalendarAddRequest;
import com.capstone.norush2025.dto.request.CalendarUpdateRequest;
import com.capstone.norush2025.dto.response.CalendarResponse;
import com.capstone.norush2025.exception.BusinessLogicException;
import com.capstone.norush2025.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * DTO를 recode 방식에서 클래스 + static inner class로 변경
 * */
@Service
@RequiredArgsConstructor
@Slf4j

public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserService userService;
    private final Optional<GoogleCalendarService> googleCalendarService;

    public CalendarResponse.CalendarInfo addCalendarEntry(String userId, CalendarAddRequest request, Optional<OAuth2AuthenticationToken> authentication) {

        User user = userService.getUser(userId);

        Calendar newCalendarEntry = Calendar.builder()
                .userId(user.getUserId())
                .year(request.getStartTime().getYear())
                .month(request.getStartTime().getMonthValue())
                .day(request.getStartTime().getDayOfMonth())
                .memo(request.getMemo())
                .build();

        Calendar savedCalendarEntry = calendarRepository.save(newCalendarEntry);

        if (user.getProvider() == AuthProvider.GOOGLE && authentication.isPresent() && googleCalendarService.isPresent()) {
            try {

                String summary = "[NoRush] " + request.getMemo(); 

                googleCalendarService.get().createCalendarEvent(
                    authentication.get(), 
                    summary, 
                    request.getMemo(),
                    request.getStartTime(),
                    request.getEndTime()
                );
                log.info("Google Calendar에 일정 동기화 성공 (사용자: {})", userId);
            } catch (Exception e) {

                log.error("Google Calendar 동기화 실패 (사용자: {}): {}", userId, e.getMessage());
            }
        }

        return new CalendarResponse.CalendarInfo(savedCalendarEntry);
    }

    @Transactional(readOnly = true)
    public List<CalendarResponse.CalendarInfo> getCalendarEntries(String userId, Integer year, Integer month) {

        User user = userService.getUser(userId);

        List<Calendar> calendarEntries;
        if (year != null && month != null) {
            calendarEntries = calendarRepository.findByUserIdAndYearAndMonth(user.getUserId(), year, month);
        } else {
            calendarEntries = calendarRepository.findByUserId(user.getUserId());
        }

        return calendarEntries.stream()
                .map(CalendarResponse.CalendarInfo::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 소유한 특정 캘린더 일정을 조회해서 응답DTO(CalendarInfo)로 반환
     * */
    @Transactional(readOnly = true)
    public CalendarResponse.CalendarInfo getCalendarEntryByCalendarIdAndUserId(String calendarId, String userId) {

        User user = userService.getUser(userId);

        Calendar calendarEntry = findCalendarEntryByCalendarIdAndUserId(calendarId, user.getUserId());

        return new CalendarResponse.CalendarInfo(calendarEntry);
    }

    @Transactional
    public CalendarResponse.CalendarInfo updateCalendarEntry(String calendarId, String userId, CalendarUpdateRequest request,
        Optional<OAuth2AuthenticationToken> authentication) {

        User user = userService.getUser(userId);

        Calendar existingCalendarEntry = findCalendarEntryByCalendarIdAndUserId(calendarId, user.getUserId());

        existingCalendarEntry.updateCalendar(
            request.getStartTime().getYear(),
            request.getStartTime().getMonthValue(),
            request.getStartTime().getDayOfMonth(),
            request.getMemo()
        );

        Calendar updatedCalendarEntry = calendarRepository.save(existingCalendarEntry);

        if (user.getProvider() == AuthProvider.GOOGLE && authentication.isPresent() && googleCalendarService.isPresent()) {
        try {
            // GoogleCalendarService에 updateCalendarEvent 메서드 구현 필요
            // log.info("Google Calendar 일정 수정 동기화 시도...");
        } catch (Exception e) {
            log.error("Google Calendar 수정 동기화 실패: {}", e.getMessage());
        }
        }
        return new CalendarResponse.CalendarInfo(updatedCalendarEntry);
    }

    @Transactional
    public void deleteCalendarEntry(String calendarId, String userId) {

        User user = userService.getUser(userId);

        Calendar calendarEntryToDelete = findCalendarEntryByCalendarIdAndUserId(calendarId, user.getUserId());

        calendarRepository.delete(calendarEntryToDelete);
    }

    private Calendar findCalendarEntryByCalendarIdAndUserId(String calendarId, String userId) {
        return calendarRepository.findByCalendarIdAndUserId(calendarId, userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NOT_FOUND_ERROR.getMessage()));

    }
}
