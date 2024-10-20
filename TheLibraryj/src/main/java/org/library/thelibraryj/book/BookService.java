package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.*;
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

    void resetBookPreviewsCache();
}
