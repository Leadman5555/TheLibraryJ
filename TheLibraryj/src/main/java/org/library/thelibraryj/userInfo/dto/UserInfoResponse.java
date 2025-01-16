package org.library.thelibraryj.userInfo.dto;

import java.time.Instant;

public record UserInfoResponse(String username, String email, int rank, int currentScore, Instant dataUpdatedAt, String status, short preference) {
}
