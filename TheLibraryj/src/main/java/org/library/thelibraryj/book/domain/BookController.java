package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}/books")
record BookController(BookService bookService) implements ErrorHandling {


    @GetMapping
    public List<BookPreviewResponse> getBookPreviews(){
        return bookService.getBookPreviewResponses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id){
        return handle(bookService.getBookDetailResponse(id));
    }


}
