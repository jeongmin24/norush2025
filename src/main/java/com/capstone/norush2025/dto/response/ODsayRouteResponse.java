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
        private Integer searchType; //결과 구분 0-도시내 1-도시간직통 2-도시간환승
        private Integer outTrafficCheck; //도시간 직통 탐색 결과 유무 0-False 1-True
        private Integer busCount; // 버스 결과 개수
        private Integer subwayCount; // 지하철 결과 개수
        private Integer subwayBusCount; //버스+지하철 결과개수
        private Double pointDistance; //출발지(SX,XY)와 도착지(EX,EY)의 직선거리 (m)
        private Integer startRadius; //출발지 반경
        private Integer endRadius; //도착지 반경
        private List<Path> path; // 후보경로 리스트
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Path {
        private Integer pathType; // 결과 종류 1-지하철 2-버스 3-지하철+버스
        private Info info; // 요약 정보
        private List<SubPath> subPath; // 이동 교통수단 정보
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class SubPath {
        private Integer trafficType; // 이동수단 종류 1-지하철 2-버스 3-도보
        private Double distance;   //이동거리(m)
        private Integer stationCount; //이동하여 정차하는 정거장수 (지하철,버스인 경우만 필수)
        private Integer sectionTime;    //이동소요시간
        private List<Lane> lane;  //교통수단정보확장
        //intervalTime
        private Integer intervalTime; //평균 배차간격(분)
        private String startName; // 승차정류장/역 명
        private Double startX; //승차정류장/역 X좌표
        private Double startY; //승차정류장/역 Y좌표
        private String endName; // 하차정류장/역 명
        private Double endX; //하차정류장/역 X좌표
        private Double endY; //하차정류장/역 Y좌표
        private String way; //방면 정보 (지하철)
        private Integer wayCode; //방면 정보 코드 1-상행 2-하행
        private String door; // 지하철 빠른 환승위치
        //startID
        private Integer startID; //출발 정류장/역 코드
        //endID
        private Integer endID; //도착 정류장/역 코드
        private String startExitNo; //지하철 들어가는 출구번호
        private Double startExitX; // 지하철 들어가는 출구 X좌표
        private Double startExitY; // 지하철 둘어가는 출구 Y좌표 (지하철인 경우에만 사용되지만 해당 태그가 없을 수도 있음)
        //EndExitNo
        private String endExitNo; //지하철 나가는 출구번호
        //endExitX
        private Double endExitX; //지하철 나가는 출구 X좌표
        //endExitY
        private Double endExitY; //지하철 나가는 출구 Y좌표
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
        private Integer totalTime; // 총 소요시간
        private Double totalDistance; // 총 이동거리
        private Integer totalWalk;  //총 도보 이동거리
        private Integer payment;    //총 요금
        private Integer busTransitCount;    //버스 환승 횟수
        private Integer subwayTransitCount; //지하철 환승 횟수
        private String mapObj; //세부경로 요청 api 파라미터
        private String firstStartStation; //최초 출발역/정류장
        private String lastEndStation; //최종 도착역/정류장
        private Integer totalStationCount; //총 정류장 합
        private Integer busStationCount; //버스 정류장 합
        private Integer subwayStationCount; //지하철 정류장 합
        private Double trafficDistance; //도보를 제외한 총이동거리
        private Integer checkIntervalTime; // 배차시간 간격 체크 기준 시간(분)
        private String checkIntervalTimeOverYn; //배차간격 체크 기준시간 초과 노선 여부 (Y/N)
        private Integer totalIntervalTime; // 전체 배차간격 시간(분)
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class PassStopList {
        private List<Station> stations; //정류장 정보 그룹
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Station {
        private Integer index; //정류장 순번
        private Integer stationID; //정류장 ID
        private String stationName; //정류장 명칭
        private Double x; //정류장 X좌표
        private Double y; //정류장 y좌표
        private String isNonStop; //미정차 정류자 여부(버스)
    }
}
