package org.library.thelibraryj.userInfo.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(String username, String email, int rank, int currentScore, String status, short preference, byte[] profileImage, LocalDateTime dataUpdatedAt, LocalDateTime createdAt) {
}
