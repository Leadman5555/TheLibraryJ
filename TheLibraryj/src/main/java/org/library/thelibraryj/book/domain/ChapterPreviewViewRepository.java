package org.library.thelibraryj.book.domain;

import java.util.UUID;

interface ChapterPreviewViewRepository {
    ChapterPreviewTitleView findChapterPreviewTitleAndIdByBookIdAndNumber(UUID bookId, int number);
}
