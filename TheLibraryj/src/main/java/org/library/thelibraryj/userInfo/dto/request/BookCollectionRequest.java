package org.library.thelibraryj.userInfo.dto.request;

import java.util.UUID;

public record BookCollectionRequest(String email, UUID bookId) {
}
