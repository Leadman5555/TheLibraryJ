package org.library.thelibraryj.book;

import com.blazebit.persistence.KeysetPage;
import io.vavr.control.Either;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.book.dto.bookDto.request.BookCreationRequest;
import org.library.thelibraryj.book.dto.bookDto.response.BookDetailResponse;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.book.dto.bookDto.response.BookResponse;
import org.library.thelibraryj.book.dto.bookDto.request.BookUpdateRequest;
import org.library.thelibraryj.book.dto.chapterDto.request.ChapterBatchRequest;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterResponse;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterUpsertResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.book.dto.sharedDto.request.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.sharedDto.response.ContentRemovalSuccess;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID bookId);

    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID bookId);

    boolean checkIfBookExists(UUID bookId);

    PagedBookPreviewsResponse getKeySetPagedBookPreviewResponses(KeysetPage lastPage, int page);

    PagedBookPreviewsResponse getOffsetPagedBookPreviewResponses(int pageSize, int page);

    PagedBookPreviewsResponse getByParamsOffsetPaged(@Nullable String titleLike, @Nullable Integer minChapters,
                                                     @Nullable Float minRating, @Nullable BookState state,
                                                     @Nullable BookTag[] hasTags, @Nullable Boolean sortAscByRating,
                                                     int pageSize, int page);

    PagedBookPreviewsResponse getByParamsKeySetPaged(@Nullable String titleLike, @Nullable Integer minChapters,
                                                     @Nullable Float minRating, @Nullable BookState state,
                                                     @Nullable BookTag[] hasTags, @Nullable Boolean sortAscByRating,
                                                     KeysetPage lastPage, int page);

    List<BookPreviewResponse> getBookPreviewsByAuthor(String byUser);
    List<BookPreviewResponse> getBookPreviewsByIds(Set<UUID> bookIds);
    Set<BookPreviewResponse> getBookPreviewsByIdsAsSet(Set<UUID> bookIds);
    PagedChapterPreviewResponse getOffsetPagedChapterPreviewResponses(int pageSize, int page, UUID bookId);

    PagedChapterPreviewResponse getKeySetPagedChapterPreviewResponses(KeysetPage lastPage, int page, UUID bookId);

    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);

    Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest);

    Either<GeneralError, BookResponse> getBook(String title);

    List<RatingResponse> getRatingResponsesForBook(UUID bookId);

    Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest);

    Either<GeneralError, ChapterResponse> getChapterByBookIdAndNumber(UUID bookId, int chapterNumber);

    Either<GeneralError, List<ChapterUpsertResponse>> upsertChapters(ChapterBatchRequest createChapterBatchRequest);

    Either<GeneralError, ContentRemovalSuccess> deleteChapter(ContentRemovalRequest contentRemovalRequest, int chapterNumber);

    Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest contentRemovalRequest);

    void updateAllForNewUsername(UUID forUserId, String newUsername);

    void resetBookPreviewsCache();
}
