package com.capstone.norush2025.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "bus_traffic_data")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class BusTrafficData {
    @Id
    private String busTrafficDataId;

    private String vehicleId;
    private String routeId;
    private String routeName;

    private String currentStopId;
    private String currentStopName;
    private String nextStopId;
    private String nextStopName;

    private Double congestionLevel;

    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;

    private String dayOfWeek;

    private LocalTime firstBusTime;
    private LocalTime lastBusTime;
    private Integer dispatchInterval; // 배차간격 (분 단위)
}
