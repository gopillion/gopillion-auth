package com.gopillion.gopillion_auth.repository;

import com.gopillion.gopillion_auth.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {}
