package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.Calendar;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends MongoRepository<Calendar, String> {
    List<Calendar> findByUserId(String userId);

    List<Calendar> findByUserIdAndYearAndMonth(String userId, int year, int month);

    Optional<Calendar> findByCalendarIdAndUserId(String calendarId, String userId);
}
