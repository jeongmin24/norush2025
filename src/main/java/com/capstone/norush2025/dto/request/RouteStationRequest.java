package com.capstone.norush2025.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RouteStationRequest {
    @NotEmpty
    private String from;
    @NotEmpty
    private String to;
    private String datetime; // 옵션 -> null 허용
}