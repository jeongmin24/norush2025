package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.RouteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RouteRepository extends MongoRepository<RouteDocument, String> {
    List<RouteDocument> findByUserId(String userId);
}
