package org.library.thelibraryj.book.domain;

import java.util.UUID;

interface BookViewRepository {
    ChapterPreviewTitleView findChapterPreviewTitleAndIdByBookIdAndNumber(UUID bookId, int number);
    NotificationEssentialsView findNotificationEssentialsViewByBookId(UUID bookId);
}
