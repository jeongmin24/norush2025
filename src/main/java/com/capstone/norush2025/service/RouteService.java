package com.capstone.norush2025.service;

import com.capstone.norush2025.dto.response.ODsayRouteResponse;
import com.capstone.norush2025.dto.response.RouteResponse;
import com.capstone.norush2025.infra.ODSayClient;
import com.capstone.norush2025.infra.TmapClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.capstone.norush2025.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.RouteMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final TmapClient tmapClient;
    private final ODSayClient odsayClient;
    //private final PredictClient predictClient;
    private final ObjectMapper objectMapper; // 주입받으면 자동 빈 사용 가능
    private final UserService userService;

    // α, β는 가중치 — 프로젝트 설정값으로 빼도 좋음
    private static final double TIME_WEIGHT = 0.6;
    private static final double CONGESTION_WEIGHT = 0.4;

    public RouteResponse findRoute(String userId, double startX, double startY, double endX, double endY) {

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
        List<RouteResponse.RouteInfo> routeInfoList = itineraries.stream()
                .map(it -> objectMapper.convertValue(it, RouteResponse.RouteInfo.class))
                .toList();

        // RouteResponse 생성
        return RouteResponse.builder()
                .message("Tmap 요청 성공")
                .routes(routeInfoList)
                .build();


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

}

