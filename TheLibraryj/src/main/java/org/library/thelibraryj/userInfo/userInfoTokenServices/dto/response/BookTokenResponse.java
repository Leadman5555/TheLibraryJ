package org.library.thelibraryj.userInfo.userInfoTokenServices.dto.response;

import java.time.Instant;

public record BookTokenResponse(String token, Instant expiresAt, int useCount, boolean justCreated) {
}
