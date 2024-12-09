package org.library.thelibraryj.book.dto.sharedDto;

import java.util.UUID;

public record ContentRemovalSuccess(UUID bookId, String userEmail) {}
