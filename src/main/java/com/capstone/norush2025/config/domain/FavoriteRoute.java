package com.capstone.norush2025.domain;

import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "favorite_routes")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRoute extends BaseEntity {

    @Id
    private String favoriteRouteId;

    private String userId;  // 누구의 즐겨찾기인지
    private String name;    // 출근길, 퇴근길...
    private String type;    // "BUS" or "SUBWAY"
    private String memo;

    private String routeId; // 즐겨찾기할 노선 ID (ex. 123번, 2호선 등)
    private String startStopName;
    private String endStopName;

    public void updateFavoriteRoute(String name, String type, String routeId, String memo) {
        this.name = name;
        this.type = type;
        this.routeId = routeId;
        this.memo = memo;
    }


}
