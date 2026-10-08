package com.gopillion.gopillion_auth.service;

import com.gopillion.gopillion_auth.config.AuthProperties;
import com.gopillion.gopillion_auth.entity.AuthSession;
import com.gopillion.gopillion_auth.entity.RefreshToken;
import com.gopillion.gopillion_auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final AuthProperties props;
    private final SecureRandom random = new SecureRandom();

    /** Creates a refresh token in a new family; returns the RAW token (only its hash is stored). */
    public String issueNewFamily(AuthSession session) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setTokenId(UUID.randomUUID());
        rt.setSessionId(session.getSessionId());
        rt.setUserId(session.getUserId());
        rt.setTokenHash(Hashing.sha256Hex(raw));
        rt.setFamilyId(UUID.randomUUID());
        rt.setExpiresAt(Instant.now().plus(Duration.ofDays(props.refresh().ttlDays())));
        rt.setRevoked(false);
        rt.setCreatedAt(Instant.now());
        repo.save(rt);
        return raw;
    }
}
