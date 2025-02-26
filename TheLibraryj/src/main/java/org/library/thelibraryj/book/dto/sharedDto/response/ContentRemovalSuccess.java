package org.library.thelibraryj.book.dto.sharedDto.response;

import java.util.UUID;

public record ContentRemovalSuccess(UUID bookId, String userEmail) {}
