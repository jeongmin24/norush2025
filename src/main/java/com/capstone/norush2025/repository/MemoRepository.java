package com.capstone.norush2025.repository;

import com.capstone.norush2025.domain.Memo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends MongoRepository<Memo, String> {
    List<Memo> findByUserId(String userId);

    Optional<Memo> findByMemoIdAndUserId(String memoId, String userId);

    Optional<Memo> findByUserIdAndTitle(String userId, String title);
}
