package com.gopillion.gopillion_auth.client;

import java.util.Optional;

/** Contract to the User Service (GET /internal/users/by-phone/{phone}). */
public interface UserClient {
    Optional<UserSummary> findByPhone(String phoneNumber);
}
