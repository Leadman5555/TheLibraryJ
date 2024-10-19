package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookRequest;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailResponse> getBookDetailResponse(UUID detailId);
    Either<GeneralError, BookPreviewResponse> getBookPreviewResponse(UUID previewId);
    List<BookPreviewResponse> getBookPreviewResponses();
    Either<GeneralError, BookResponse> createBook(BookCreationRequest bookCreationRequest);
    Either<GeneralError, BookResponse> updateBook(BookRequest bookRequest);
    Either<GeneralError, BookResponse> getBook(String title);
}
