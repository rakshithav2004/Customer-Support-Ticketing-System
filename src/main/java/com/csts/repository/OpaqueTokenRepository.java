package com.csts.repository;

import com.csts.security.OpaqueToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OpaqueTokenRepository extends MongoRepository<OpaqueToken, String> {
    OpaqueToken findByToken(String token);
    List<OpaqueToken> findByExpiresAtBefore(LocalDateTime time);
}