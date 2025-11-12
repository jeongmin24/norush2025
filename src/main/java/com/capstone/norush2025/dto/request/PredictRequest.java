package com.capstone.norush2025.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictRequest {
    private Integer hourOfDay; // 예측 시점 (메인서버에서 현재 시각을 넘겨줌)
    private List<Route> routes; // 여러 후보 경로 (path)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Route { // 후보 경로 하나
        private Integer routeType; // 1-지하철 2-버스 3-지하철+버스
        private List<Section> sections; // 한 경로의 구간별 정보 (subPath)
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Section  {
        private Integer trafficType; // 1-지하철, 2-버스, 3-도보
        private List<Lane> lanes; //같은 구간을 지나는 노선

        private Double distance; //이동거리
        private Integer sectionTime; //이동소요시간
        private Integer stationCount; // 이동하여 정차하는 정거장수

        private String way; //방면 정보 (지하철)
        private Integer wayCode; //방면 정보 1-상행 2-하행

        private String startName; // 승차정류장/역 명
        private Double startX; // 승차정류장/역 X좌표
        private Double startY; // 승차정류장/역 Y좌표

        private String endName; // 하차정류장/역 명
        private Double endX; //하차정류장/역 X좌표
        private Double endY; //하차정류장/역 Y좌표

        private List<Station> passStopList; // 경로 상세구간
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Lane {
        private String name;      // 지하철 노선명
        private String busNo;     // 버스 번호
        private Integer subwayCode;
        private Integer busID;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Station {
        private Integer index;       // 정류장 순서
        private Integer stationID;   // 고유 ID (지하철역 or 버스정류장)
        private String stationName;  // 정류장 이름
    }
}
