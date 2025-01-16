package org.library.thelibraryj.book;

import com.blazebit.persistence.KeysetPage;
import io.vavr.control.Either;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.book.dto.bookDto.BookCreationRequest;
import org.library.thelibraryj.book.dto.bookDto.BookDetailResponse;
import org.library.thelibraryj.book.dto.bookDto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.bookDto.BookResponse;
import org.library.thelibraryj.book.dto.bookDto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.chapterDto.ChapterRequest;
import org.library.thelibraryj.book.dto.chapterDto.ChapterResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalSuccess;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId);

    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID previewId);

    PagedBookPreviewsResponse getKeySetPagedBookPreviewResponses(@Nullable KeysetPage lastPage, int page);

    PagedBookPreviewsResponse getOffsetPagedBookPreviewResponses(int pageSize, int page);

    List<BookPreviewResponse> getByParams(@Nullable String titleLike, @Nullable Integer minChapters,
                                          @Nullable Float minRating, @Nullable BookState state,
                                          @Nullable BookTag[] hasTags, @Nullable Boolean ratingOrder);

    List<BookPreviewResponse> getBookPreviewsByAuthor(String byUser);
    PagedChapterPreviewResponse getOffsetPagedChapterPreviewResponses(int pageSize, int page, UUID bookId);

    PagedChapterPreviewResponse getKeySetPagedChapterPreviewResponses(KeysetPage lastPage, int page, UUID bookId);

    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);

    Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest);

    Either<GeneralError, BookResponse> getBook(String title);

    List<RatingResponse> getRatingResponsesForBook(UUID bookId);

    Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest);

    Either<GeneralError, ChapterResponse> getChapterByBookIdAndNumber(UUID bookId, int chapterNumber);

    Either<GeneralError, ChapterPreviewResponse> createChapter(ChapterRequest chapterRequest);

    Either<GeneralError, List<ChapterPreviewResponse>> createChapters(List<ChapterRequest> chapterRequests);

    Either<GeneralError, ContentRemovalSuccess> deleteChapter(ContentRemovalRequest contentRemovalRequest, int chapterNumber);

    Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest contentRemovalRequest);

    void updateAllForNewUsername(UUID forUserId, String newUsername);

    void resetBookPreviewsCache();
}
