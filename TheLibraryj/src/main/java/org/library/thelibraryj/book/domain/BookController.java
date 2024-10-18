package org.library.thelibraryj.book.domain;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorWrapper;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("${library.mapping}/books")
class BookController implements ErrorHandling {
    private final BookService bookService;

    @GetMapping
    public List<BookPreviewResponse> getBookPreviews(){
        return bookService.getBookPreviewResponses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id){
        return bookService.getBookDetailResponse(id)
                .mapLeft(ApiErrorWrapper::new)
                .fold(this::createErrorResponse, this::createSuccessResponse);
    }


}
