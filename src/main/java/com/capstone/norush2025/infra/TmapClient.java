package com.capstone.norush2025.infra;

import com.capstone.norush2025.dto.response.RouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * tmap 서버로 api 요청
 * 추후 비동기방식 고려
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class TmapClient {

    private final WebClient webClient;

    @Value("${tmap.api.key}")
    private String apiKey;

    public Map<String, Object> requestTransitRoute(double startX, double startY, double endX, double endY) {


        log.info("🌍 [TmapClient] WebClient 요청 시작");

        // tmap 요청 본문
        Map<String, Object> body = Map.of(
                "startX", startX,
                "startY", startY,
                "endX", endX,
                "endY", endY,
                "count", 1,
                "lang", 0,
                "format", "json",
                "resCoordType", "WGS84GEO",   // 위경도 좌표계 (좌표계 지정)
                "reqCoordType", "WGS84GEO",   // 요청 좌표계 일치
                "detailPosFlag", "Y"           // 세부 위치 좌표 포함
        );

        // WebClient로 HTTP POST 요청
        return webClient.post()
                .uri("https://apis.openapi.sk.com/transit/routes?version=1&format=json")
                .header("appKey", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(e -> log.error("[Tmap 요청 실패] {}", e))
                .block();
    }
}
