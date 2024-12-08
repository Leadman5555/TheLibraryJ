package org.library.thelibraryj.book;

import com.blazebit.persistence.KeysetPage;
import io.vavr.control.Either;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterRequest;
import org.library.thelibraryj.book.dto.ChapterResponse;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.ContentRemovalSuccess;
import org.library.thelibraryj.book.dto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId);

    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID previewId);

    PagedBookPreviewsResponse getKeySetPagedBookPreviewResponses(@Nullable KeysetPage lastPage, int pageSize, int page);

    List<BookPreviewResponse> getByParams(@Nullable String titleLike, @Nullable Integer minChapters, @Nullable Float minRating, @Nullable BookState state, @Nullable BookTag[] hasTags);

    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);

    Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest);

    Either<GeneralError, BookResponse> getBook(String title);

    Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest);

    Either<GeneralError, ChapterResponse> getChapterByBookIdAndNumber(UUID bookId, int chapterNumber);

    Either<GeneralError, ChapterPreviewResponse> createChapter(ChapterRequest chapterRequest);

    Either<GeneralError, List<ChapterPreviewResponse>> createChapters(List<ChapterRequest> chapterRequests);

    Either<GeneralError, ContentRemovalSuccess> deleteChapter(ContentRemovalRequest contentRemovalRequest, int chapterNumber);

    Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest contentRemovalRequest);

    void updateAllForNewUsername(UUID forUserId, String newUsername);

    void resetBookPreviewsCache();
}
