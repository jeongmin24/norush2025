package com.capstone.norush2025.dto.request;

import lombok.Getter;
import lombok.Setter;
/**
 * 추후 검증 어노테이션 추가 고려
 * */
@Getter
@Setter
public class FavoriteRouteAddRequest {
    private String name;
    private String type;      // BUS or SUBWAY
    private String routeId;   // 경로 ID (버스/지하철 등)
    private String memo;
//    private String startStopName;
//    private String endStopName;
}
