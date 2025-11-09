package com.capstone.norush2025.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ODSayClient {

    private final WebClient webClient;

    @Value("${odsay.api.key}")
    private String apiKey;



    /**
     * ODsay 대중교통 길찾기 API 호출 (GET 방식)
     */
    public Map<String, Object> requestTransitRoute(double startX, double startY, double endX, double endY) {

        try {
            log.info("[ODsay 요청 시작] 출발=({}, {}), 도착=({}, {})", startX, startY, endX, endY);

            // GET 요청 + 쿼리 파라미터 방식
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.odsay.com")
                            .path("/v1/api/searchPubTransPathT")
                            .queryParam("apiKey", apiKey)
                            .queryParam("SX", startX)
                            .queryParam("SY", startY)
                            .queryParam("EX", endX)
                            .queryParam("EY", endY)
                            .queryParam("OPT", 1)        // 0: 추천, 1: 타입별정렬
                            .queryParam("output", "json")
                            .build())
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("ODsay API 오류: " + body))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .doOnSuccess(res -> log.info("[ODsay 요청 성공]"))
                    .doOnError(e -> log.error("[ODsay 요청 실패]: {}", e.getMessage()))
                    .block();

            // 결과 로그
            if (response != null) {
                log.info("ODsay 응답: {}", response);
            } else {
                log.warn("ODsay 응답이 null입니다 (IP 또는 파라미터 문제 가능)");
            }

            return response;

        } catch (Exception e) {
            log.error("[ODsayClient] 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("ODsay API 요청 중 오류 발생", e);
        }
    }
}


