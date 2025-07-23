package com.csts.repository;

import com.csts.security.OpaqueToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpaqueTokenRepository extends MongoRepository<OpaqueToken, String> {
    OpaqueToken findByToken(String token);
}