package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.FavoriteRoute;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepository extends MongoRepository<FavoriteRoute, String> {
    List<FavoriteRoute> findByUserId(String userId);

    Optional<FavoriteRoute> findByFavoriteRouteIdAndUserId(String favoriteRouteId, String userId);

    Optional<FavoriteRoute> findByUserIdAndName(String userId, String name);
}
