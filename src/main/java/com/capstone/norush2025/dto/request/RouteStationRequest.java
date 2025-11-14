package com.capstone.norush2025.dto.request;

import lombok.Data;

@Data
public class RouteStationRequest {
    private String from;
    private String to;
    private String datetime; // 옵션 -> null 허용
}