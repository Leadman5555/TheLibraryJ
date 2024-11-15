package org.library.thelibraryj.userInfo.dto;

import java.time.Instant;
import java.util.UUID;

public record UserInfoWithImageResponse(UUID userId, UUID userAuthId, String username, String email, int rank, Instant dataUpdatedAt, byte[] profileImage) {}
