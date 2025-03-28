package org.library.thelibraryj.book.dto.chapterDto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChapterPreviewResponse(int number, String title, boolean isSpoiler, UUID chapterId, LocalDateTime updatedAt) {
}
