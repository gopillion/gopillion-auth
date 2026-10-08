CREATE TABLE auth_sessions (
  session_id   UUID PRIMARY KEY,
  user_id      UUID NOT NULL,
  device_id    VARCHAR(100) NOT NULL,
  device_info  VARCHAR(255),
  fcm_token    TEXT,
  ip_address   VARCHAR(45),
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_seen_at TIMESTAMPTZ,
  is_active    BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_sessions_user ON auth_sessions(user_id);

CREATE TABLE refresh_tokens (
  token_id    UUID PRIMARY KEY,
  session_id  UUID NOT NULL REFERENCES auth_sessions(session_id),
  user_id     UUID NOT NULL,
  token_hash  VARCHAR(64) NOT NULL UNIQUE,
  family_id   UUID NOT NULL,
  expires_at  TIMESTAMPTZ NOT NULL,
  is_revoked  BOOLEAN NOT NULL DEFAULT FALSE,
  replaced_by UUID,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_rt_user ON refresh_tokens(user_id);
CREATE INDEX idx_rt_family ON refresh_tokens(family_id);

CREATE TABLE otp_logs (
  otp_log_id    UUID PRIMARY KEY,
  phone_number  VARCHAR(15) NOT NULL,
  purpose       VARCHAR(10) NOT NULL,
  is_verified   BOOLEAN NOT NULL DEFAULT FALSE,
  attempt_count INT NOT NULL DEFAULT 0,
  ip_address    VARCHAR(45),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_otp_phone ON otp_logs(phone_number);

CREATE TABLE user_roles (
  user_id         UUID PRIMARY KEY,
  driver_verified BOOLEAN NOT NULL DEFAULT FALSE,
  is_banned       BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
