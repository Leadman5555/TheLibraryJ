package org.library.thelibraryj.userInfo.dto.response;

import java.time.LocalDateTime;

public record UserUsernameUpdateResponse(String newUsername, LocalDateTime dataUpdatedAt) {
}
