package com.capstone.norush2025.infra;

import com.capstone.norush2025.dto.request.PredictRequest;
import com.capstone.norush2025.dto.response.PredictResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Builder
public class PredictClient {
    private final WebClient webClient;

    //path 리스트 요청으로 보냄
    /**
     * [
     *   { "routeType": 1, "sections": [...] },
     *   { "routeType": 2, "sections": [...] }
     * ]
     * */
    public Mono<PredictResponse> requestCongestion(List<PredictRequest.Route> routes) {
        PredictRequest request = PredictRequest.builder()
                .routes(routes)
                .build();
        return webClient.post()
                .uri("https://fastapi-server-6oi6.onrender.com/api/v1/predict/congestion") // 혼잡도 서버 주소
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("예측 서버 오류: " + msg))
                )
                .bodyToMono(PredictResponse.class); // 응답서버에서 PredictResponse를 반환
    }
}
