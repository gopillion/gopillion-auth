package com.gopillion.gopillion_auth.service;

import com.gopillion.gopillion_auth.client.UserClient;
import com.gopillion.gopillion_auth.client.UserSummary;
import com.gopillion.gopillion_auth.dto.SendOtpRequest;
import com.gopillion.gopillion_auth.dto.VerifyOtpRequest;
import com.gopillion.gopillion_auth.dto.VerifyOtpResponse;
import com.gopillion.gopillion_auth.entity.AuthSession;
import com.gopillion.gopillion_auth.exception.ApiException;
import com.gopillion.gopillion_auth.exception.ErrorCode;
import com.gopillion.gopillion_auth.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthSessionRepository sessionRepo;
    private final UserClient userClient;

    public void sendOtp(SendOtpRequest req) {
        otpService.sendOtp(normalize(req.phoneNumber()));
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest req, String ip) {
        String phone = normalize(req.phoneNumber());
        otpService.verifyOtp(phone, req.otp());

        Optional<UserSummary> found = userClient.findByPhone(phone);
        if (found.isEmpty()) {
            // New user: no account yet; they must complete the profile with this short-lived token
            return new VerifyOtpResponse(true, null, null, jwtService.createTempToken(phone));
        }

        UserSummary user = found.get();
        if (user.banned()) throw new ApiException(ErrorCode.USER_BANNED);

        AuthSession session = sessionRepo.save(AuthSession.create(
                user.userId(), req.deviceId(), req.deviceInfo(), req.fcmToken(), ip));

        // TODO(step 4): read driverVerified from user_roles (fed by DriverVerifiedEvent). false until KYC exists.
        String access = jwtService.createAccessToken(user.userId(), session.getSessionId(),
                user.activeMode() != null ? user.activeMode() : "RIDER", false);
        String refresh = refreshTokenService.issueNewFamily(session);
        return new VerifyOtpResponse(false, access, refresh, null);
    }

    /** Canonical form: +<digits>. A bare 10-digit number is assumed to be Indian (+91). */
    static String normalize(String raw) {
        String digits = raw.replaceAll("[^0-9+]", "");
        if (digits.startsWith("+")) return digits;
        if (digits.length() == 10) return "+91" + digits;
        if (digits.length() == 12 && digits.startsWith("91")) return "+" + digits;
        throw new ApiException(ErrorCode.INVALID_PHONE);
    }
}
