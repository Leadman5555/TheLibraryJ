package org.library.thelibraryj.authentication.userAuth.dto;

import org.library.thelibraryj.authentication.userAuth.domain.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserCreationResponse(UUID userId, String username, String email, int rank, UserRole role, boolean isEnabled, Instant dataUpdatedAt) {
}
