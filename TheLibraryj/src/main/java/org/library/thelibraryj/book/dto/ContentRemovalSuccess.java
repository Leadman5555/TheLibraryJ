package org.library.thelibraryj.book.dto;

import java.util.UUID;

public record ContentRemovalSuccess(UUID bookId, UUID authorId) {}
