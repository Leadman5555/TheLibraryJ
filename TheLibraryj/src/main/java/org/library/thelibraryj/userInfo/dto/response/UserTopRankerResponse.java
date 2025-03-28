package org.library.thelibraryj.userInfo.dto.response;

public record UserTopRankerResponse(String username, short rank, int currentScore, short preference, byte[] profileImage) {
}
