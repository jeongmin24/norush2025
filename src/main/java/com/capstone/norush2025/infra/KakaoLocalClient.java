package com.capstone.norush2025.infra;

import com.capstone.norush2025.common.Coordinate;
import com.capstone.norush2025.dto.response.KakaoKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoLocalClient {

    @Value("${kakao.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public Coordinate search(String keyword) {

        int maxRetry = 3;      // 최대 재시도 횟수
        long delayMs = 300;    // 기본 딜레이 (ms)

        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;

        for (int i = 0; i < maxRetry; i++) {

            try {
                // --- 실제 호출 ---
                KakaoKeywordResponse response = webClient.get()
                        .uri(url)
                        .header("Authorization", "KakaoAK " + apiKey)
                        .retrieve()
                        .bodyToMono(KakaoKeywordResponse.class)
                        .block();

                // --- 결과 없음 처리 ---
                if (response == null || response.getDocuments().isEmpty()) {
                    throw new RuntimeException("검색 결과가 없습니다: " + keyword);
                }

                // --- 정상 반환 ---
                KakaoKeywordResponse.KakaoPlace place = response.getDocuments().get(0);

                double lng = Double.parseDouble(place.getX());
                double lat = Double.parseDouble(place.getY());

                return new Coordinate(lat, lng);

            } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {

                // --- 429 Too Many Requests 감지 ---
                if (e.getStatusCode().value() == 429) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ignored) {}

                    delayMs *= 2;  // 점진적 backoff
                    continue;      // 다음 루프에서 재시도
                }

                // 429가 아니라면 바로 throw (또는 원하는 에러로 감싸기)
                throw e;
            }
        }

        throw new RuntimeException("카카오 API 429 오류로 검색 실패: " + keyword);
    }
}
