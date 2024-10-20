package org.library.thelibraryj.book.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChapterPreviewResponse(int number, String title, UUID chapterId, LocalDateTime updatedAt) {
}
