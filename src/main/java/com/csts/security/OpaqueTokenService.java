package com.csts.security;

import com.csts.model.User;
import com.csts.repository.OpaqueTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

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
        opaqueToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        repository.save(opaqueToken);

        return token;
    }
    public OpaqueToken validateToken(String token) {
        OpaqueToken ot = repository.findByToken(token);
        if (ot != null && ot.getExpiresAt().isAfter(LocalDateTime.now())) {
            return ot;
        }
        return null;
    }
}
