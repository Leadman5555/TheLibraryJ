package org.library.thelibraryj.userInfo.dto.request;

import java.util.UUID;

public record UserInfoRequest(String username, String email, UUID userAuthId) {
}
