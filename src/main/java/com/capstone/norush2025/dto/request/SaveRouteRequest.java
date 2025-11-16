package com.capstone.norush2025.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveRouteRequest {
    @NotEmpty
    private Double startX;
    @NotEmpty
    private Double startY;
    @NotEmpty
    private Double endX;
    @NotEmpty
    private Double endY;
}