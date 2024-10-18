package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.GeneralError;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId);
    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID previewId);
    List<BookPreviewResponse> getBookPreviews();
    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);
    Either<GeneralError, BookResponse> updateBook(BookRequest bookRequest);
}
