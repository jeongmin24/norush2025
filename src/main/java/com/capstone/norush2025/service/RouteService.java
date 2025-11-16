package com.capstone.norush2025.service;

import com.capstone.norush2025.domain.RouteDocument;
import com.capstone.norush2025.dto.request.PredictRequest;
import com.capstone.norush2025.dto.request.SaveRouteRequest;
import com.capstone.norush2025.dto.response.ODsayRouteResponse;
import com.capstone.norush2025.dto.response.PredictResponse;
import com.capstone.norush2025.dto.response.RouteResponse;
import com.capstone.norush2025.dto.response.TmapRouteResponse;
import com.capstone.norush2025.infra.ODSayClient;
import com.capstone.norush2025.infra.PredictClient;
import com.capstone.norush2025.infra.TmapClient;
import com.capstone.norush2025.repository.RouteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.capstone.norush2025.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final TmapClient tmapClient;
    private final ODSayClient odsayClient;
    private final PredictClient predictClient;
    private final ObjectMapper objectMapper; // 주입받으면 자동 빈 사용 가능
    private final UserService userService;
    private final RouteRepository routeRepository;

    // α, β는 가중치 — 프로젝트 설정값으로 빼도 좋음
    private static final double TIME_WEIGHT = 0.6;
    private static final double CONGESTION_WEIGHT = 0.4;

    public TmapRouteResponse findRoute(String userId, double startX, double startY, double endX, double endY) {

        User user = userService.getUser(userId);

        System.out.println("[RouteService] Tmap 요청 시작");
        System.out.println("요청자: " + user.getUserId());
        System.out.println("요청자 이름: " + user.getName());

        // tmap 서버 호출
        Map<String, Object> responseMap = tmapClient.requestTransitRoute(startX, startY, endX, endY);

        // metaData → plan → itineraries 추출
        Map<String, Object> metaData = (Map<String, Object>) responseMap.get("metaData");
        if (metaData == null) throw new RuntimeException("metaData 없음");

        Map<String, Object> plan = (Map<String, Object>) metaData.get("plan");
        if (plan == null) throw new RuntimeException("plan 없음");

        List<Map<String, Object>> itineraries = (List<Map<String, Object>>) plan.get("itineraries");

        System.out.println("📦 [RouteService] itineraries 개수: " + itineraries.size());

        // itineraries JSON → RouteResponse.RouteInfo 리스트로 변환
        List<TmapRouteResponse.RouteInfo> routeInfoList = itineraries.stream()
                .map(it -> objectMapper.convertValue(it, TmapRouteResponse.RouteInfo.class))
                .toList();

        // RouteResponse 생성
        return TmapRouteResponse.builder()
                .message("Tmap 요청 성공")
                .routes(routeInfoList)
                .build();


    }

    // 경로 저장
    public Long saveRoute(String userId, SaveRouteRequest req) {
        RouteDocument doc = RouteDocument.builder()
                .userId(userId)
                .startX(req.getStartX())
                .startY(req.getStartY())
                .endX(req.getEndX())
                .endY(req.getEndY())
                .build();

        RouteDocument saved = routeRepository.save(doc);
        return saved.getRouteId();
    }


    public ODsayRouteResponse findRouteByOdsay(String userId, double startX, double startY, double endX, double endY) {
        log.info("👤 사용자: {}, ODsay 경로 조회 start=({}, {}), end=({}, {})", userId, startX, startY, endX, endY);

        // Map 형태로 응답 받기
        Map<String, Object> rawResponse = odsayClient.requestTransitRoute(startX, startY, endX, endY);

        // Map → DTO 변환
        ODsayRouteResponse dto = objectMapper.convertValue(rawResponse, ODsayRouteResponse.class);

        // 로깅용 요약
        if (dto.getResult() != null && dto.getResult().getPath() != null) {
            log.info("🛤 후보경로 개수: {}", dto.getResult().getPath().size());
            dto.getResult().getPath().forEach(path -> {
                if (path.getInfo() != null) {
                    log.info(" - [{}] {}분, 요금 {}원, 환승 {}회",
                            path.getPathType(),
                            path.getInfo().getTotalTime(),
                            path.getInfo().getPayment(),
                            path.getInfo().getBusTransitCount() + path.getInfo().getSubwayTransitCount());
                }
            });
        }

        return dto;
    }

    public RouteResponse findRouteWithCongestion(String userId, double startX, double startY, double endX, double endY) {
        log.info("👤 사용자: {}, ODsay 경로 조회 start=({}, {}), end=({}, {})", userId, startX, startY, endX, endY);

        // Map 형태로 응답 받기
        Map<String, Object> rawResponse = odsayClient.requestTransitRoute(startX, startY, endX, endY);

        // Map → DTO 변환
        ODsayRouteResponse dto = objectMapper.convertValue(rawResponse, ODsayRouteResponse.class);

        //ODsayRouteResponse -> PredictRequest 매핑
        List<PredictRequest.Route> routes = convertOdsayToPredictRequest(dto);

        //PredictClient 호출 -> PredictResponse 응답
        PredictResponse response = predictClient.requestCongestion(routes).block();

        if (response == null || response.getRoutes() == null) {
            throw new IllegalStateException("예측 결과가 없습니다.");
        }

        log.info("예측 결과: {}" ,response);

        // ODsay + Predict 응답 -> RouteResponse
        RouteResponse combinedRouteResponse = combineRouteAndPrediction(dto, response);

//        // 모든 경로의 소요시간 계산
//        List<PredictResponse.Route> allRoutes = response.getRoutes();
//        int minTime = allRoutes.stream()
//                .mapToInt(this::getTotalTime)
//                .min()
//                .orElse(Integer.MAX_VALUE);
//
//        int timeThreshold = minTime + 30; // 가장 빠른 경로 + 30분 기준
//
//        log.info("🚦 최소 소요시간: {}분, 필터 기준(≤ {}분)", minTime, timeThreshold);
//
//        // 기준시간이내 + 혼잡도 낮은순 정렬
//        List<PredictResponse.Route> sortedRoutes = allRoutes.stream()
//                .filter(route -> getTotalTime(route) <= timeThreshold)
//                .sorted(Comparator.comparingDouble(this::getAverageCongestion))
//                .toList();

        // 결과반환
        return combinedRouteResponse;
    }

    /**
     * ODsayResponse -> PredictRequest 구조로 변환
     * */
    private List<PredictRequest.Route> convertOdsayToPredictRequest(ODsayRouteResponse dto) {
        if (dto == null || dto.getResult() == null || dto.getResult().getPath() == null) {
            throw new IllegalArgumentException("ODsay 응답이 올바르지 않습니다.");
        }

        return dto.getResult().getPath().stream()
                .map(path -> {
                    // Path → Route
                    List<PredictRequest.Section> sections = path.getSubPath().stream()
                            .map(sub -> {
                                // SubPath → Section
                                List<PredictRequest.Lane> lanes = (sub.getLane() == null)
                                        ? List.of()
                                        : sub.getLane().stream()
                                        .map(lane -> PredictRequest.Lane.builder()
                                                .name(lane.getName())
                                                .busNo(lane.getBusNo())
                                                .subwayCode(lane.getSubwayCode())
                                                .busID(lane.getBusID())
                                                .build())
                                        .toList();

                                List<PredictRequest.Station> stations = (sub.getPassStopList() == null
                                        || sub.getPassStopList().getStations() == null)
                                        ? List.of()
                                        : sub.getPassStopList().getStations().stream()
                                        .map(st -> PredictRequest.Station.builder()
                                                .index(st.getIndex())
                                                .stationID(st.getStationID())
                                                .stationName(st.getStationName())
                                                .build())
                                        .toList();

                                return PredictRequest.Section.builder()
                                        .trafficType(sub.getTrafficType())
                                        .distance(sub.getDistance())
                                        .sectionTime(sub.getSectionTime())
                                        .stationCount(sub.getStationCount())
                                        .way(sub.getWay())
                                        .wayCode(sub.getWayCode())
                                        .startName(sub.getStartName())
                                        .startX(sub.getStartX())
                                        .startY(sub.getStartY())
                                        .endName(sub.getEndName())
                                        .endX(sub.getEndX())
                                        .endY(sub.getEndY())
                                        .lanes(lanes)
                                        .passStopList(stations)
                                        .build();
                            })
                            .toList();

                    return PredictRequest.Route.builder()
                            .routeType(path.getPathType())
                            .sections(sections)
                            .build();
                })
                .toList();
    }

    /**
     * ODsay 경로 응답 + Predict 예측 응답을 RouteResponse로 빌더 매핑
     * */
    // ODsay 응답 + 예측 응답 → RouteResponse
    private RouteResponse combineRouteAndPrediction(
            ODsayRouteResponse odsay,
            PredictResponse predict) {

        ODsayRouteResponse.Result result = odsay.getResult();

        // 경로(path) 리스트 매핑
        List<RouteResponse.Route> routes = mapRoutes(
                result.getPath(),
                predict.getRoutes()
        );

        // 정렬 적용
        List<RouteResponse.Route> sortedRoutes = sortRoutesByTimeAndCongestion(routes);

        // 최종 응답 DTO 조립
        return RouteResponse.builder()
                .message("혼잡도 예측 및 경로 정렬 성공")
                .result(new RouteResponse.Result(
                        result.getSearchType(),
                        result.getOutTrafficCheck(),
                        result.getBusCount(),
                        result.getSubwayCount(),
                        result.getSubwayBusCount(),
                        result.getPointDistance(),
                        result.getStartRadius(),
                        result.getEndRadius(),
                        sortedRoutes // 최종 경로 리스트
                ))
                .build();
    }

    // List<Route>매핑 odsay의 List<path>와 predict의 List<route>
    private List<RouteResponse.Route> mapRoutes(
            List<ODsayRouteResponse.Path> odsayPaths,
            List<PredictResponse.Route> predictRoutes) {

        if (odsayPaths == null || predictRoutes == null) return List.of();

        int count = Math.min(odsayPaths.size(), predictRoutes.size());
        List<RouteResponse.Route> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            result.add(mapRoute(odsayPaths.get(i), predictRoutes.get(i)));
        }

        return result;
    }


    // route 매핑 (odsay dto의 path와 predict response의 route)
    private RouteResponse.Route mapRoute(
            ODsayRouteResponse.Path odsayPath,
            PredictResponse.Route predictRoute) {

        List<RouteResponse.Section> sections = mapSections(
                odsayPath.getSubPath(),
                predictRoute.getSections()
        );

        return RouteResponse.Route.builder()
                .routeType(odsayPath.getPathType())
                .info(mapInfo(odsayPath.getInfo()))
                .section(sections)
                .build();
    }

    // Info 매핑 함수 (ODsay → RouteResponse)
    private RouteResponse.Info mapInfo(ODsayRouteResponse.Info info) {
        if (info == null) return null;

        return RouteResponse.Info.builder()
                .totalTime(info.getTotalTime())
                .totalDistance(info.getTotalDistance())
                .totalWalk(info.getTotalWalk())
                .payment(info.getPayment())
                .busTransitCount(info.getBusTransitCount())
                .subwayTransitCount(info.getSubwayTransitCount())
                .mapObj(info.getMapObj())
                .firstStartStation(info.getFirstStartStation())
                .lastEndStation(info.getLastEndStation())
                .totalStationCount(info.getTotalStationCount())
                .busStationCount(info.getBusStationCount())
                .subwayStationCount(info.getSubwayStationCount())
                .trafficDistance(info.getTrafficDistance())
                .checkIntervalTime(info.getCheckIntervalTime())
                .checkIntervalTimeOverYn(info.getCheckIntervalTimeOverYn())
                .totalIntervalTime(info.getTotalIntervalTime())
                .build();
    }


    // List<Section> 매핑
    private List<RouteResponse.Section> mapSections(
            List<ODsayRouteResponse.SubPath> odsayList,
            List<PredictResponse.Section> predictList) {

        // 리스트가 비어있으면 빈 리스트 반환
        if (odsayList == null || predictList == null) return List.of();

        int count = Math.min(odsayList.size(), predictList.size());
        List<RouteResponse.Section> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            RouteResponse.Section section = mapSection(odsayList.get(i), predictList.get(i));
            result.add(section);
        }

        return result; // 모든 구간(section) 리스트 반환
    }


    // section 매핑 (odsay subPath와 predict의 section)
    private RouteResponse.Section mapSection(ODsayRouteResponse.SubPath odsay, PredictResponse.Section predict) {

        // 리스트가 비어있으면 빈 리스트 반환
        List<RouteResponse.Station> stations = mapStations(
                odsay.getPassStopList() != null ? odsay.getPassStopList().getStations() : null,
                predict.getPassStopList()
        );

        // 혼잡도 요약 (있을 경우)
        RouteResponse.SectionSummary summary = null;
        if (predict.getSectionSummary() != null) {
            PredictResponse.SectionSummary ps = predict.getSectionSummary();
            summary = RouteResponse.SectionSummary.builder()
                    .avgCongestion(ps.getAvgCongestion())
                    .maxCongestion(ps.getMaxCongestion())
                    .totalExpectedBoarding(ps.getTotalExpectedBoarding())
                    .totalExpectedAlighting(ps.getTotalExpectedAlighting())
                    .startStation(RouteResponse.StartEndStation.builder()
                            .name(ps.getStartStation().getName())
                            .expectedBoarding(ps.getStartStation().getExpectedBoarding())
                            .expectedAlighting(ps.getStartStation().getExpectedAlighting())
                            .build())
                    .endStation(RouteResponse.StartEndStation.builder()
                            .name(ps.getEndStation().getName())
                            .expectedBoarding(ps.getEndStation().getExpectedBoarding())
                            .expectedAlighting(ps.getEndStation().getExpectedAlighting())
                            .build())
                    .build();
        }

        return RouteResponse.Section.builder()
                .trafficType(odsay.getTrafficType())
                .distance(odsay.getDistance())
                .stationCount(odsay.getStationCount())
                .sectionTime(odsay.getSectionTime())
                .startName(odsay.getStartName())
                .endName(odsay.getEndName())
                .way(odsay.getWay())
                .wayCode(odsay.getWayCode())
                .passStopList(RouteResponse.PassStopList.builder()
                        .stations(stations)
                        .build())
                .sectionSummary(summary)
                .build();

    }

    // List<Station> 매핑
    private List<RouteResponse.Station> mapStations(
            List<ODsayRouteResponse.Station> osList,
            List<PredictResponse.Station> psList) {

        // 리스트가 하나라도 null이면 빈 리스트 반환
        if (osList == null || psList == null) return List.of();

        // 크기가 다를 수 있으니 공통된 최소 크기만큼만 순회
        int count = Math.min(osList.size(), psList.size());
        List<RouteResponse.Station> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            RouteResponse.Station merged = mapStation(osList.get(i), psList.get(i));
            result.add(merged);
        }

        return result;

    }

    // station 매핑
    private RouteResponse.Station mapStation(ODsayRouteResponse.Station os, PredictResponse.Station ps) {
        return RouteResponse.Station.builder()
                .index(os.getIndex())
                .stationID(os.getStationID())
                .stationName(os.getStationName())
                .x(os.getX())
                .y(os.getY())
                .isNonStop(os.getIsNonStop()) // ui 정보는 os에서
                .expectedBoarding(ps.getExpectedBoarding())
                .expectedAlighting(ps.getExpectedAlighting())
                .predictedCongestionCar(ps.getPredictedCongestionCar()) // 예상 승하차 인원은 ps에서
                .build();
    }



    /**
     * 경로 정렬 함수 (RouteResponse 안의 routes 정렬)
     * */
    private List<RouteResponse.Route> sortRoutesByTimeAndCongestion(List<RouteResponse.Route> routes) {
        if (routes == null || routes.isEmpty()) return List.of();

        // 1. 최소 소요시간 찾기
        int minTime = routes.stream()
                .mapToInt(r -> r.getInfo() != null ? r.getInfo().getTotalTime() : Integer.MAX_VALUE)
                .min()
                .orElse(Integer.MAX_VALUE);

        // 2. 30분(=1800초) 이내 경로 필터링
        List<RouteResponse.Route> filtered = routes.stream()
                .filter(r -> r.getInfo() != null && r.getInfo().getTotalTime() <= minTime + 30) //초 단위의 경우 1800
                .toList();

        // 3. 혼잡도 계산 및 정렬
        return filtered.stream()
                .sorted(Comparator
                        .comparingDouble(this::calculateAverageCongestion) // 혼잡도 낮은 순
                        .thenComparingInt(r -> r.getInfo().getTotalTime())) // 시간이 짧은 순
                .toList();
    }

    // 경로의 평균 혼잡도 계산
    private double calculateAverageCongestion(RouteResponse.Route route) {
        if (route.getSection() == null || route.getSection().isEmpty()) return Double.MAX_VALUE;

        List<RouteResponse.Section> sections = route.getSection();

        double total = 0;
        int count = 0;

        for (RouteResponse.Section s : sections) {
            if (s.getSectionSummary() != null && s.getSectionSummary().getAvgCongestion() != null) {
                total += s.getSectionSummary().getAvgCongestion();
                count++;
            }
        }

        return count == 0 ? Double.MAX_VALUE : total / count;
    }


}

