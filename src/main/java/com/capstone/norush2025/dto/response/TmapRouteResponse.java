package com.capstone.norush2025.dto.response;

import lombok.*;

import java.util.List;

/**
 * Tmap 응답 매핑용 DTO - tmap 응답 형식 그대로
 * 대중교통 api의 plan(itineraries)을 가져옴
 * */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TmapRouteResponse {

    private String message;
    private List<RouteInfo> routes; // tmap에서 여러개의 경로를 응답

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteInfo {
        private Integer pathType;       // 경로 유형 (도보/버스/지하철 등 복합)
        private Integer transferCount;      // 환승 횟수
        private Integer totalTime;       // 전체 소요시간 (분)
        private Integer totalDistance;      // 총 이동거리 (m)
        private Integer totalWalkTime;     // 전체 도보 시간 (분)
        private Integer totalWalkDistance; // 전체 도보 거리 (m)
        private Fare fare;                 // 요금 정보
        private List<Leg> legs; // 한 경로의 세부 경로
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Fare {
        private Regular regular; // 금액 상위노드

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Regular {
            private Integer totalFare; //대중교통요금
            private Currency currency; //금액 상위노드
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Currency {
            private String symbol; // 금액상징($)
            private String currency; // 금액단위(원)
            private String currencyCode; //금액단위코드(KRW)
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Leg {
        private String mode;              // WALK, BUS, SUBWAY, EXP, TRAIN, AIRPLANE, FERRY
        private Integer sectionTime; // 구간별 소요시간(분)
        private Integer distance;   // 구간별 이동거리 (m)
        private String route;             // 노선 명칭
        private String routeColor;         // 노선 색상
        private String routeId;             // 노선 ID
        private String type;        // 이동수단별 노선코드 (1-도보, 2-버스, 3-지하철, 4-고속/시외버스, 5-기차, 6-항공, 7-해운)
        private Integer routePayment;   //광역이동수단 요금
        private Integer service;        // 이동수단 운행여부 (1:운행중/0:운행종료)
        private List<Lane> lane;          // 구간 내 여러 노선
        private StartEndPoint start; //구간별 출발정보
        private StartEndPoint end;  //구간별 도착정보
        private List<Step> steps;         // 도보 구간 상세
        private PassShape passShape;        // 경로선분
        private PassStopList passStopList;      // 대중교통 구간 정류장 정보
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Lane {
        private Integer service;
        private String route;
        private String routeColor;
        private String routeId;
        private String type;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StartEndPoint {
        private String name;
        private Double lon;
        private Double lat;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Step {
        private Double distance;
        private String streetName;
        private String description;
        private String linestring;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassShape {
        private String linestring;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassStopList {
        private List<Station> stations;
    }


    // 정류장 상세 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Station {
        private Integer index;
        private String stationID;
        private String stationName;
        private String lon; // String으로 응답받음
        private String lat; // String
    }


}
