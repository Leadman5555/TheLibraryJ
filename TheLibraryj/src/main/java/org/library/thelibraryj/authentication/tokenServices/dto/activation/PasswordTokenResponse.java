package org.library.thelibraryj.authentication.tokenServices.dto.activation;

import java.time.Instant;

public record PasswordTokenResponse(String token, Instant expiresAt) {
}
