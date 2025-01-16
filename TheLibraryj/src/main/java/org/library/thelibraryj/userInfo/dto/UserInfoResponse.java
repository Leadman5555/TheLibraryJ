package org.library.thelibraryj.userInfo.dto;

import java.time.LocalDateTime;

public record UserInfoResponse(String username, String email, int rank, int currentScore, LocalDateTime dataUpdatedAt, String status, short preference) {
}
