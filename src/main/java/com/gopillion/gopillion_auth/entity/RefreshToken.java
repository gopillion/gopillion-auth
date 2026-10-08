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
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id private UUID tokenId;
    private UUID sessionId;
    private UUID userId;
    private String tokenHash;
    private UUID familyId;
    private Instant expiresAt;
    @Column(name = "is_revoked") private boolean revoked;
    private UUID replacedBy;
    private Instant createdAt;
}
