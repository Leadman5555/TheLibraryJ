package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;

import java.util.List;
import java.util.UUID;

interface BookBlazeRepository {
    void updateAllForNewUsername(UUID forUserId, String newUsername);
    PagedList<BookPreview> getKeySetPagedBookPreviewNext(KeysetPage page, int pageNumber);
    PagedList<BookPreview> getOffsetBookPreviewPaged(int pageSize, int pageNumber);

    PagedList<ChapterPreview> getKeySetPagedChapterPreviewNext(KeysetPage page, int pageNumber, UUID bookId);
    PagedList<ChapterPreview> getOffsetChapterPreviewPaged(int pageSize, int pageNumber, UUID bookId);

    List<BookPreview> getBookPreviewByParams(String titleLike, Integer minChapters, Float minRating, BookState state,
                                             BookTag[] tags, Boolean ratingOrder);
    List<BookPreview> getAuthoredBookPreviews(String byUser);
}
