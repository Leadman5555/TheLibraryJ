package org.library.thelibraryj.book.dto;

import java.time.Instant;
import java.util.UUID;

public record ChapterPreviewResponse(String title, UUID id, Instant updatedAt) {
}
