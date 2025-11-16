package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.user.AuthProvider;
import com.capstone.norush2025.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    Optional<User> findByUserId(String userId);

    boolean existsByEmail(String email);
}
