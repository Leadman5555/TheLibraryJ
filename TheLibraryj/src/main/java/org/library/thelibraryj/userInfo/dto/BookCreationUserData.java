package org.library.thelibraryj.userInfo.dto;

import java.util.UUID;

public record BookCreationUserData(UUID authorId, String authorUsername) {
}
