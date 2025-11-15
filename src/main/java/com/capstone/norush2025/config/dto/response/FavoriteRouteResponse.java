package com.capstone.norush2025.dto.response;

import com.capstone.norush2025.domain.FavoriteRoute;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class FavoriteRouteResponse {

    @Getter
    @Setter
    public static class FavoriteRouteInfo {
        private String favoriteRouteId;
        private String userId;
        private String name;
        private String type;
        private String routeId;
        private String startStopName;
        private String endStopName;
        private String memo;
        private LocalDateTime createdAt;

        public FavoriteRouteInfo(FavoriteRoute favoriteRoute) {
            this.favoriteRouteId = favoriteRoute.getFavoriteRouteId();
            this.userId = favoriteRoute.getUserId();
            this.name = favoriteRoute.getName();
            this.type = favoriteRoute.getType();
            this.routeId = favoriteRoute.getRouteId();
            this.startStopName = favoriteRoute.getStartStopName();
            this.endStopName = favoriteRoute.getEndStopName();
            this.memo = favoriteRoute.getMemo();
            this.createdAt = favoriteRoute.getCreatedAt();
        }
    }
}
