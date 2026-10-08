package com.gopillion.gopillion_auth.client;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory stand-in until the User Service exists. */
@Component
public class StubUserClient implements UserClient {

    private final Map<String, UserSummary> users = new ConcurrentHashMap<>();

    public StubUserClient() {
        // seeded user so the "existing user" login path can be tested
        users.put("+919999999999",
                new UserSummary(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "+919999999999", "RIDER", false));
    }

    @Override
    public Optional<UserSummary> findByPhone(String phoneNumber) {
        return Optional.ofNullable(users.get(phoneNumber));
    }
}
