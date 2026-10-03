CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- AUTH USERS
-- Stores the authentication identity of a GoPillion user.
-- Profile/business data belongs to gopillion-user.
-- ============================================================

CREATE TABLE auth_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    phone_number VARCHAR(20) NOT NULL UNIQUE,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_auth_users_status
        CHECK (status IN ('PENDING', 'ACTIVE', 'BLOCKED'))
);


-- ============================================================
-- OTP CHALLENGES
-- Stores hashed OTP challenges used for authentication.
-- Never store the actual OTP.
-- ============================================================

CREATE TABLE otp_challenges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    phone_number VARCHAR(20) NOT NULL,

    otp_hash VARCHAR(255) NOT NULL,

    purpose VARCHAR(30) NOT NULL,

    expires_at TIMESTAMPTZ NOT NULL,

    attempts INTEGER NOT NULL DEFAULT 0,

    verified_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_otp_purpose
        CHECK (purpose IN ('LOGIN', 'SIGNUP', 'PHONE_VERIFICATION')),

    CONSTRAINT chk_otp_attempts
        CHECK (attempts >= 0)
);


CREATE INDEX idx_otp_challenges_phone_number
    ON otp_challenges(phone_number);

CREATE INDEX idx_otp_challenges_expires_at
    ON otp_challenges(expires_at);


-- ============================================================
-- REFRESH TOKENS
-- Stores hashed refresh tokens for session management.
-- The actual refresh token is never stored in the database.
-- ============================================================

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    auth_user_id UUID NOT NULL,

    token_hash VARCHAR(255) NOT NULL UNIQUE,

    device_id VARCHAR(255),

    expires_at TIMESTAMPTZ NOT NULL,

    revoked_at TIMESTAMPTZ,

    replaced_by_token_id UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_tokens_auth_user
        FOREIGN KEY (auth_user_id)
        REFERENCES auth_users(id)
        ON DELETE CASCADE
);


CREATE INDEX idx_refresh_tokens_auth_user_id
    ON refresh_tokens(auth_user_id);

CREATE INDEX idx_refresh_tokens_expires_at
    ON refresh_tokens(expires_at);