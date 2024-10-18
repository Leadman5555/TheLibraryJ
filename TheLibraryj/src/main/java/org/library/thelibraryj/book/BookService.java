package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.GeneralError;

import java.util.List;
import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailsResponse> getBookDetail(UUID detailsId);
    List<BookPreviewResponse> getBookPreviews();
}
