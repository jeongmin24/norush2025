package com.capstone.norush2025.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// public class CalendarAddRequest {


//     @NotNull(message = "연도를 입력해주세요.")
//     @Min(value = 2025, message = "연도는 2025년 이후여야 합니다.")
//     private Integer year;

//     @NotNull(message = "월을 입력해주세요.")
//     @Min(value = 1, message = "월은 1월 이상이어야 합니다.")
//     @Max(value = 12, message = "월은 12월 이하여야 합니다.")
//     private Integer month;

//     @NotNull(message = "일을 입력해주세요.")
//     @Min(value = 1, message = "일은 1일 이상이어야 합니다.")
//     @Max(value = 31, message = "일은 31일 이하여야 합니다.")
//     private Integer day;

//     @Size(max = 100, message = "메모는 최대 100자까지 가능합니다.")
//     private String memo;

// }

public class CalendarAddRequest {

    @NotNull(message = "시작 시간을 입력해주세요.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간을 입력해주세요.")
    private LocalDateTime endTime;

    @Size(max = 100, message = "메모는 최대 100자까지 가능합니다.")
    private String memo;
}