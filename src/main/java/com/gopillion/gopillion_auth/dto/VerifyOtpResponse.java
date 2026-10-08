package com.gopillion.gopillion_auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerifyOtpResponse(boolean isNewUser, String accessToken, String refreshToken, String tempToken) {}
