package com.capstone.norush2025.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ODsayRouteResponse {
    private Result result;

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Result {
        private List<Path> path; // 결과리스트
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Path {
        private int pathType; // 결과 종류 1-지하철 2-버스 3-지하철+버스
        private List<SubPath> subPath; // 이동 교통수단 정보 확장
        private Info info; // 요약 정보 확장
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class SubPath {
        private int trafficType; // 이동수단 종류 1-지하철 2-버스 3-도보
        private double distance;   //이동거리
        private int sectionTime;    //이동소요시간
        private List<Lane> lane;  //교통수단정보확장
        private String startName; // 승차정류장/역 명
        private double startX; //승차정류장/역 X좌표
        private double startY; //승차정류장/역 Y좌표
        private String endName; // 하차정류장/역 명
        private double endX; //하차정류장/역 X좌표
        private double endY; //하차정류장/역 Y좌표
        private String way; //방면 정보 (지하철)
        private Integer wayCode; //방면 정보 코드 1-상행 2-하행
        private String door; // 지하철 빠른 환승위치
        private String startExitNo; //지하철 출구번호
        private double startExitX; // 지하철 출구 X좌표
        private double startExitY; // 지하철 출구 Y좌표 (지하철인 경우에만 사용되지만 해당 태그가 없을 수도 있음)
        private PassStopList passStopList; //경로 상세구간 정보
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Lane {
        private String name; // 지하철 노선명
        private String nameKor; //지하철 노선명 국문
        private String busNo; //버스 번호
        private String busNoKor; //버스 번호 국문
        private Integer subwayCode; // 지하철 노선 번호
        private Integer subwayCityCode;
        private Integer busID; //버스 코드
        private Integer busCityCode; //운수 회사 승인 도시코드 (버스)
        private String busLocalBlID; //각 지역 버스노선 ID
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Info {
        private int totalTime; // 총 소요시간
        private double totalDistance; // 총 이동거리
        private int totalWalk;  //총 도보 이동거리
        private int payment;    //총 요금
        private int busTransitCount;    //버스 환승 횟수
        private int subwayTransitCount; //지하철 환승 횟수
        private int checkIntervalTime; // 배차시간 간격 체크 기준 시간(분)
        private String checkIntervalTimeOverYn; //배차간격 체크 기준시간 초과 노선 여부 (Y/N)
        private int totalIntervalTime; // 전체 배차간격 시간(분)
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class PassStopList {
        private List<Station> stations; //정류장 정보 그룹
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Station {
        private int index; //정류장 순번
        private String stationName; //정류장 명칭
        private double x; //정류장 X좌표
        private double y; //정류장 y좌표
        private String isNonStop; //미정차 정류자 여부(버스)
    }
}
