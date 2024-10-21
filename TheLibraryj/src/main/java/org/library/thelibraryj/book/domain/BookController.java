package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.ChapterRequest;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PatchMapping("/book")
    public ResponseEntity<String> updateBook(@RequestBody BookUpdateRequest bookUpdateRequest) {
        return handle(bookService.updateBook(bookUpdateRequest));
    }

    @PutMapping("/rating")
    public ResponseEntity<String> upsertRating(@RequestBody RatingRequest ratingRequest) {
        return handle(bookService.upsertRating(ratingRequest));
    }

    @PutMapping("/book/chapter")
    public ResponseEntity<String> upsertRating(@RequestBody ChapterRequest chapterRequest) {
        return handle(bookService.createChapter(chapterRequest));
    }

    @PutMapping("/book/chapter/batch")
    public ResponseEntity<String> upsertRating(@RequestBody List<ChapterRequest> chapterRequests) {
        return handle(bookService.createChapters(chapterRequests));
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
