package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterRequest;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.ContentRemovalSuccess;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId);

    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID previewId);

    List<BookPreviewResponse> getBookPreviewResponses();

    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);

    Either<GeneralError, BookResponse> updateBook(BookUpdateRequest bookUpdateRequest);

    Either<GeneralError, BookResponse> getBook(String title);

    Either<GeneralError, RatingResponse> upsertRating(RatingRequest ratingRequest);
    Either<GeneralError, ChapterPreviewResponse> createChapter(ChapterRequest chapterRequest);
    Either<GeneralError, List<ChapterPreviewResponse>> createChapters(List<ChapterRequest> chapterRequests);

    Either<GeneralError, ContentRemovalSuccess> deleteChapter(ContentRemovalRequest contentRemovalRequest, int chapterNumber);

    Either<GeneralError, ContentRemovalSuccess> deleteBook(ContentRemovalRequest contentRemovalRequest);

    void resetBookPreviewsCache();
}
