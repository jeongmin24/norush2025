package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.BusTrafficData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BusTrafficDataRepository extends MongoRepository<BusTrafficData, String> {

    List<BusTrafficData> findByRouteId(String routeId);

    List<BusTrafficData> findByRouteIdAndCurrentStopId(String routeId, String currentStopId);

    List<BusTrafficData> findByRouteIdAndCurrentStopIdAndTimestampBetween(String routeId, String currentStopId, LocalDateTime timestampAfter, LocalDateTime timestampBefore);

    Optional<BusTrafficData> findTopByVehicleIdOrderByTimestampDesc(String vehicleId);

}
