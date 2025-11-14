package com.capstone.norush2025.service;

import com.capstone.norush2025.common.Coordinate;
import com.capstone.norush2025.infra.KakaoLocalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationCoordinateService {

    private final KakaoLocalClient kakaoLocalClient;

    /**
     * 역 이름 또는 장소 이름을 받아 좌표(lat, lng)를 반환
     */
    public Coordinate findCoordinate(String stationName) {
        return kakaoLocalClient.search(stationName);
    }
}
