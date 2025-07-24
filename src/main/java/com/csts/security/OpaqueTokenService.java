package com.csts.security;

import com.csts.model.User;
import com.csts.repository.OpaqueTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class OpaqueTokenService {
    @Autowired
    private OpaqueTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateToken(User user) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        OpaqueToken opaqueToken = new OpaqueToken();
        opaqueToken.setToken(token);
        opaqueToken.setUserId(user.getId());
        opaqueToken.setRole(user.getRole().name());
        opaqueToken.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        repository.save(opaqueToken);
        return token;
    }
    public void invalidateToken(String token) {
        try {
            OpaqueToken opaqueToken = repository.findByToken(token);
            if (opaqueToken != null) {
                repository.delete(opaqueToken);
            }
        } catch (Exception e) {
            System.err.println("Failed to invalidate token: " + e.getMessage());
        }
    }
    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<OpaqueToken> expiredTokens = repository.findByExpiresAtBefore(now);
        if (!expiredTokens.isEmpty()) {
            repository.deleteAll(expiredTokens);
        }
    }
    public OpaqueToken validateToken(String token) {
        OpaqueToken ot = repository.findByToken(token);
        if (ot != null && ot.getExpiresAt().isAfter(LocalDateTime.now())) {
            return ot;
        }
        return null;
    }
}
