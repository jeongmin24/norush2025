package com.capstone.norush2025.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRouteUpdateRequest {
    private String name;
    private String type;
    private String routeId;
    private String memo;
}
