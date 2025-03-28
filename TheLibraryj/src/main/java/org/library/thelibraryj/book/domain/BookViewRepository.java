package org.library.thelibraryj.book.domain;

import java.util.UUID;

interface BookViewRepository {
    ChapterPreviewContentView findChapterPreviewContentByBookIdAndNumber(UUID bookId, int number);
    NotificationEssentialsView findNotificationEssentialsViewByBookId(UUID bookId);
}
