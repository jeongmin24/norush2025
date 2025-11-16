package com.capstone.norush2025.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoKeywordResponse {
    private List<KakaoPlace> documents;

    @Data
    public static class KakaoPlace {
        private String x; // 경도
        private String y; // 위도
        private String place_name; // 옵션 (필요하면)
    }

}
