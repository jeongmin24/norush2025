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

        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;

        KakaoKeywordResponse response = webClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .bodyToMono(KakaoKeywordResponse.class)
                .block();

        if (response == null || response.getDocuments().isEmpty()) {
            throw new RuntimeException("검색 결과가 없습니다: " + keyword);
        }

        KakaoKeywordResponse.KakaoPlace place = response.getDocuments().get(0);

        double lng = Double.parseDouble(place.getX());
        double lat = Double.parseDouble(place.getY());

        return new Coordinate(lat, lng);
    }
}
