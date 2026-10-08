package com.gopillion.gopillion_auth.controller;

import com.gopillion.gopillion_auth.dto.SendOtpRequest;
import com.gopillion.gopillion_auth.dto.VerifyOtpRequest;
import com.gopillion.gopillion_auth.dto.VerifyOtpResponse;
import com.gopillion.gopillion_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Phone OTP login and signup")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;

    @Operation(summary = "Send OTP to a phone number",
            description = "Max 3 sends per phone per 10 minutes. OTP valid for 5 minutes.")
    @ApiResponse(responseCode = "204", description = "OTP sent")
    @ApiResponse(responseCode = "400", description = "INVALID_PHONE")
    @ApiResponse(responseCode = "429", description = "OTP_RATE_LIMITED")
    @PostMapping("/otp/send")
    public ResponseEntity<Void> sendOtp(@Valid @RequestBody SendOtpRequest req) {
        authService.sendOtp(req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verify OTP",
            description = "Existing user: returns accessToken + refreshToken. "
                    + "New user: returns isNewUser=true and a short-lived tempToken for /auth/signup/complete.")
    @ApiResponse(responseCode = "200", description = "Verified")
    @ApiResponse(responseCode = "400", description = "OTP_INVALID or OTP_EXPIRED")
    @ApiResponse(responseCode = "403", description = "USER_BANNED")
    @ApiResponse(responseCode = "429", description = "OTP_ATTEMPTS_EXCEEDED")
    @PostMapping("/otp/verify")
    public VerifyOtpResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest req, HttpServletRequest http) {
        return authService.verifyOtp(req, http.getRemoteAddr());
    }
}
