package org.library.thelibraryj.authentication.tokenServices.dto.activation;

import java.time.Instant;

public record ActivationTokenResponse(String token, Instant expiresAt) {
}
