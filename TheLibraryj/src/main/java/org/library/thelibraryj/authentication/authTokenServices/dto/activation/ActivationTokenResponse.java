package org.library.thelibraryj.authentication.authTokenServices.dto.activation;

import java.time.Instant;

public record ActivationTokenResponse(String token, Instant expiresAt) {
}
