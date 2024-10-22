package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}/books")
record BookController(BookService bookService) implements ErrorHandling {


    @GetMapping
    public List<BookPreviewResponse> getBookPreviews() {
        return bookService.getBookPreviewResponses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id) {
        return handle(bookService.getBookDetailResponse(id));
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<String> getBookPreview(@PathVariable UUID id) {
        return handle(bookService.getBookPreviewResponse(id));
    }

    @GetMapping("/book/{title}")
    public ResponseEntity<String> getBookDetailById(@PathVariable String title) {
        return handle(bookService.getBook(title));
    }

    @PostMapping("/book")
    public ResponseEntity<String> createBook(@RequestBody BookCreationRequest bookCreationRequest) {
        return handle(bookService.createBook(bookCreationRequest));
    }

    @PostMapping("/book/chapter")
    public ResponseEntity<String> createChapter(@RequestBody ChapterRequest chapterRequest) {
        return handle(bookService.createChapter(chapterRequest));
    }

    @PostMapping("/book/chapter/batch")
    public ResponseEntity<String> createChapters(@RequestBody List<ChapterRequest> chapterRequests) {
        return handle(bookService.createChapters(chapterRequests));
    }

    @PatchMapping("/book")
    public ResponseEntity<String> updateBook(@RequestBody BookUpdateRequest bookUpdateRequest) {
        return handle(bookService.updateBook(bookUpdateRequest));
    }

    @PutMapping("/rating")
    public ResponseEntity<String> upsertRating(@RequestBody RatingRequest ratingRequest) {
        return handle(bookService.upsertRating(ratingRequest));
    }


    @PutMapping("/flush")
    public ResponseEntity<String> flushPreviewsCache() {
        bookService.resetBookPreviewsCache();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/book/chapter/{number}")
    public ResponseEntity<String> deleteChapter(@PathVariable Integer number, @RequestBody ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteChapter(contentRemovalRequest, number));
    }

    @DeleteMapping("/book")
    public ResponseEntity<String> deleteBook(@RequestBody ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteBook(contentRemovalRequest));
    }

}
