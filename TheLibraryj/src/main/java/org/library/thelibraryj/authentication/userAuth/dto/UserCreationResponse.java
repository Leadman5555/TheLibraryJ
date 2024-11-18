package org.library.thelibraryj.authentication.userAuth.dto;

import org.library.thelibraryj.authentication.userAuth.domain.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserCreationResponse(UUID userAuthId, String username, int rank, Instant dataUpdatedAt, String email,
                                   boolean isEnabled, UserRole role, byte[] profileImage) {
}
