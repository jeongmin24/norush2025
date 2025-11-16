package com.capstone.norush2025.dto.request;

//import com.capstone.norush2025.common.Point;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteRequest {
    private Point origin; //출발지점
    private Point destination; //도착지점
    private LocalDateTime departureTime; //출발시간

    // common > Point 고려, 경로 저장 시 Point가 필요한 경우
    @Data
    public static class Point {
        private double lat;
        private double lng;
    }
}
