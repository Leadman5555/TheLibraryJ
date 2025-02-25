package org.library.thelibraryj.userInfo.dto.request;

import java.util.UUID;

public record FavouriteBookRequest(String email, UUID bookId) {
}
