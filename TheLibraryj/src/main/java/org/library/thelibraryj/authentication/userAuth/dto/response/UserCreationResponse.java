package org.library.thelibraryj.authentication.userAuth.dto.response;

import java.time.Instant;

public record UserCreationResponse(String username, int rank, int currentScore, Instant dataUpdatedAt, String email,
                                   boolean isEnabled, byte[] profileImage) {
}
