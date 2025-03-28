package org.library.thelibraryj.userInfo.dto.request;

import java.util.UUID;

public record FavouriteBookMergerRequest(UUID fromUserId, String toUserEmail) {
}
