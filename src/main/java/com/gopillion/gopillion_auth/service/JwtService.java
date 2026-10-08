package com.gopillion.gopillion_auth.service;

import com.gopillion.gopillion_auth.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/** RS256 token issuing. Private key stays here; gateway/services verify with the public key. */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String SCOPE_ACCESS = "ACCESS";
    public static final String SCOPE_SIGNUP = "SIGNUP_ONLY";

    private final AuthProperties props;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    void initKeys() throws Exception {
        AuthProperties.Jwt cfg = props.jwt();
        if (notBlank(cfg.privateKeyPath()) && notBlank(cfg.publicKeyPath())) {
            privateKey = KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(readPem(cfg.privateKeyPath())));
            publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(readPem(cfg.publicKeyPath())));
            log.info("Loaded RSA key pair from disk");
        } else {
            log.warn("No JWT key paths configured: generating EPHEMERAL RSA keys (dev only, tokens die on restart)");
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair pair = gen.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }
    }

    public String createAccessToken(UUID userId, UUID sessionId, String activeMode, boolean driverVerified) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(props.jwt().issuer())
                .subject(userId.toString())
                .claim("scope", SCOPE_ACCESS)
                .claim("roles", List.of("RIDER", "DRIVER"))
                .claim("activeMode", activeMode)
                .claim("driverVerified", driverVerified)
                .claim("sid", sessionId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(props.jwt().accessTtlMinutes()))))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String createTempToken(String phone) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(props.jwt().issuer())
                .subject(phone)
                .claim("scope", SCOPE_SIGNUP)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(props.jwt().tempTtlMinutes()))))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(props.jwt().issuer())
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** PEM of the public key, for the gateway / a future JWKS endpoint. */
    public String publicKeyPem() {
        return "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(publicKey.getEncoded())
                + "\n-----END PUBLIC KEY-----";
    }

    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private static byte[] readPem(String path) throws Exception {
        String pem = Files.readString(Path.of(path))
                .replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }
}
