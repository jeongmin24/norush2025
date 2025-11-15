package com.capstone.norush2025.domain;

import com.capstone.norush2025.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "subway_traffic_data")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class SubwayTrafficData extends BaseEntity {
    private String subwayTrafficDataId;

    private String trainId;
    private String lineId;
    private String lineName;

    private String currentStationId;
    private String currentStationName;
    private String nextStationId;
    private String nextStationName;

    private Double congestionLevel; // 예: 1~5
    private String direction;

    private LocalDateTime timestamp;
    private String dayOfWeek;
    private Integer compartmentNumber;
}
