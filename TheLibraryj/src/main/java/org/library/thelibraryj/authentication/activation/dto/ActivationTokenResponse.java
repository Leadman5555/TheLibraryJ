package org.library.thelibraryj.authentication.activation.dto;

import java.time.Instant;

public record ActivationTokenResponse(String token, Instant expiresAt) {
}
