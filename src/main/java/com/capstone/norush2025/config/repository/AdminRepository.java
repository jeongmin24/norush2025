package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByAdminId(String adminId);
}
