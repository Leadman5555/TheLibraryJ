package org.library.thelibraryj.userInfo.dto;

public record UserInfoWithImageResponse(String username, String email, int rank, int currentScore, String status, short preference, byte[] profileImage) {}
