package org.library.thelibraryj.userInfo.dto;

import java.time.LocalDateTime;

public record UserInfoWithImageResponse(String username, String email, int rank, int currentScore, LocalDateTime dataUpdatedAt, String status, short preference, byte[] profileImage) {}
