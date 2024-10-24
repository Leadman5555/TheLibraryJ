package org.library.thelibraryj.userInfo.dto;

import java.time.Instant;
import java.util.UUID;

public record UserInfoResponse(UUID userId, String username, String email, int rank, Instant dataUpdatedAt) {
}
