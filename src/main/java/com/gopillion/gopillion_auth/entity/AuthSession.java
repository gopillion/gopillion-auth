package com.gopillion.gopillion_auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "auth_sessions")
public class AuthSession {
    @Id private UUID sessionId;
    private UUID userId;
    private String deviceId;
    private String deviceInfo;
    private String fcmToken;
    private String ipAddress;
    private Instant createdAt;
    private Instant lastSeenAt;
    @Column(name = "is_active") private boolean active;

    public static AuthSession create(UUID userId, String deviceId, String deviceInfo, String fcmToken, String ip) {
        AuthSession s = new AuthSession();
        s.sessionId = UUID.randomUUID();
        s.userId = userId;
        s.deviceId = deviceId;
        s.deviceInfo = deviceInfo;
        s.fcmToken = fcmToken;
        s.ipAddress = ip;
        s.createdAt = Instant.now();
        s.lastSeenAt = s.createdAt;
        s.active = true;
        return s;
    }
}
