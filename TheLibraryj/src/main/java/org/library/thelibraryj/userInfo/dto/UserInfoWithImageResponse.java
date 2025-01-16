package org.library.thelibraryj.userInfo.dto;

import java.time.Instant;

public record UserInfoWithImageResponse(String username, String email, int rank, int currentScore, Instant dataUpdatedAt, String status, short preference, byte[] profileImage) {}
