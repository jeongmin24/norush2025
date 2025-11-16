package com.capstone.norush2025.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
/**
 * 추후 검증 어노테이션 추가 고려
 * */
@Getter
@Setter
public class FavoriteRouteAddRequest {
    private String name;
    private String startName; //출발역
    @NotNull
    private Double startX;
    @NotNull
    private Double startY;

    private String endName; //도착역
    @NotNull
    private Double endX;
    @NotNull
    private Double endY;
}
