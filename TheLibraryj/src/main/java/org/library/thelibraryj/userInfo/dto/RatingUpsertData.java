package org.library.thelibraryj.userInfo.dto;

import java.util.UUID;

public record RatingUpsertData(UUID userId, String username) {
}
