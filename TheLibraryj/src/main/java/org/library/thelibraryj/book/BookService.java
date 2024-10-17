package org.library.thelibraryj.book;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.library.thelibraryj.infrastructure.error.GeneralError;

import java.util.UUID;

public interface BookService {
    Either<GeneralError, BookDetailsResponse> getBookDetails(UUID detailsId);
}
