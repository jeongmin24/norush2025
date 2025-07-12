package com.capstone.norush2025.dto.response;

import com.capstone.norush2025.domain.Calendar;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class CalendarResponse {

    @Getter
    @Setter
    public static class CalendarInfo {
        private String id;
        private String userId;
        private Integer year;
        private Integer month;
        private Integer day;
        private String memo;
        private LocalDateTime createdAt;

        public CalendarInfo(Calendar calendar) {
            this.id = calendar.getCalendarId();
            this.userId = calendar.getUserId();
            this.year = calendar.getYear();
            this.month = calendar.getMonth();
            this.day = calendar.getDay();
            this.memo = calendar.getMemo();
            this.createdAt = calendar.getCreatedAt();
        }
    }
}