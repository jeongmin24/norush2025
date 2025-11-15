package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.SubwayTrafficData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubwayTrafficDataRepository extends MongoRepository<SubwayTrafficData, String> {

    List<SubwayTrafficData> findByLineId(String lineId);

    List<SubwayTrafficData> findByLineIdAndCurrentStationId(String lineId, String currentStationId);

    List<SubwayTrafficData> findByLineIdAndCurrentStationIdAndTimestampBetween(String lineId, String currentStationId, LocalDateTime timestampAfter, LocalDateTime timestampBefore);

    Optional<SubwayTrafficData> findTopByTrainIdOrderByTimestampDesc(String trainId);

    List<SubwayTrafficData> findByLineIdAndCurrentStationIdAndCompartmentNumberAndTimestampBetween(String lineId, String currentStationId, Integer compartmentNumber, LocalDateTime timestampAfter, LocalDateTime timestampBefore);
}
