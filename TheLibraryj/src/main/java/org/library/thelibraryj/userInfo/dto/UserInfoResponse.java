package org.library.thelibraryj.userInfo.dto;

import java.util.UUID;

public record UserInfoResponse(UUID userId, String username) {
}
