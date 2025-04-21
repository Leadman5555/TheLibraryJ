package org.library.thelibraryj.userInfo.dto.response;

public record UserInfoWithImageResponse(String username, String email, int rank, int currentScore, String status, short preference, String profileImageUrl) {}
