package com.gopillion.gopillion_auth.client;

import java.util.UUID;

public record UserSummary(UUID userId, String phoneNumber, String activeMode, boolean banned) {}
