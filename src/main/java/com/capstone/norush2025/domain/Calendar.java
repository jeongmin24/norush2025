package com.capstone.norush2025.domain;

import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "calendars")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Calendar extends BaseEntity {
    @Id
    private String calendarId;

    private String userId;
    private int year;
    private int month;
    private int day;
    private String memo;

    public void updateCalendar(Integer year, Integer month, Integer day, String memo) {
        if (year != null) {
            this.year = year;
        }
        if (month != null) {
            this.month = month;
        }
        if (day != null) {
            this.day = day;
        }
        if (memo != null) {
            this.memo = memo;
        }
    }
}
