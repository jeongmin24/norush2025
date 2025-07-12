package com.capstone.norush2025.service;

import com.capstone.norush2025.code.ErrorCode;
import com.capstone.norush2025.domain.Calendar;
import com.capstone.norush2025.domain.user.User;
import com.capstone.norush2025.dto.request.CalendarAddRequest;
import com.capstone.norush2025.dto.request.CalendarUpdateRequest;
import com.capstone.norush2025.dto.response.CalendarResponse;
import com.capstone.norush2025.exception.BusinessLogicException;
import com.capstone.norush2025.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * DTO를 recode 방식에서 클래스 + static inner class로 변경
 * */
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserService userService;

    public CalendarResponse.CalendarInfo addCalendarEntry(String userId, CalendarAddRequest request) {

        User user = userService.getUser(userId);

        Calendar newCalendarEntry = Calendar.builder()
                .userId(user.getUserId())
                .year(request.getYear())
                .month(request.getMonth())
                .day(request.getDay())
                .memo(request.getMemo())
                .build();

        Calendar savedCalendarEntry = calendarRepository.save(newCalendarEntry);
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
    public CalendarResponse.CalendarInfo updateCalendarEntry(String calendarId, String userId, CalendarUpdateRequest request) {

        User user = userService.getUser(userId);

        Calendar existingCalendarEntry = findCalendarEntryByCalendarIdAndUserId(calendarId, user.getUserId());

        existingCalendarEntry.updateCalendar(
                request.getYear(),
                request.getMonth(),
                request.getDay(),
                request.getMemo()
        );

        Calendar updatedCalendarEntry = calendarRepository.save(existingCalendarEntry);
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
