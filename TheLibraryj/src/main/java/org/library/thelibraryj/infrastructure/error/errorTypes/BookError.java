package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface BookError extends GeneralError {
    record BookDetailEntityNotFound(UUID missingEntityId) implements BookError {}
    record BookPreviewEntityNotFound(String missingEntityIdentifier) implements BookError {}
    record DuplicateTitle(String bookIdentifier) implements BookError {}
    record UserNotAuthor(String userEmail, UUID bookId) implements BookError {}
    record ChapterNotFound(UUID bookId, int chapterNumber) implements BookError {}
    record DuplicateChapter(UUID bookId, int chapterNumber) implements BookError {}
    record InvalidChapterTitleFormat(UUID bookId, String title) implements BookError {}
    record MalformedChapterText(UUID bookId, int chapterNumber) implements BookError {}
    record InvalidChapterTextLength(UUID bookId, int chapterNumber) implements BookError {}
}
