package org.library.thelibraryj.userInfo.dto.request;

import java.util.UUID;

public record UserInfoScoreUpdateRequest(UUID forUser, UUID forAuthor, boolean hadComment) {
}
