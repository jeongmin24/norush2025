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
        private String routeId;

        private Double startX;
        private Double startY;
        private Double endX;
        private Double endY;

        private String startStopName;
        private String endStopName;

        private LocalDateTime createdAt;

        public FavoriteRouteInfo(FavoriteRoute favoriteRoute) {
            this.favoriteRouteId = favoriteRoute.getFavoriteRouteId();
            this.userId = favoriteRoute.getUserId();
            this.name = favoriteRoute.getName();
            this.startX = favoriteRoute.getStartX();
            this.startY = favoriteRoute.getStartY();
            this.endX = favoriteRoute.getEndX();
            this.endY = favoriteRoute.getEndY();
            this.startStopName = favoriteRoute.getStartStopName();
            this.endStopName = favoriteRoute.getEndStopName();
            this.createdAt = favoriteRoute.getCreatedAt();
        }
    }
}
