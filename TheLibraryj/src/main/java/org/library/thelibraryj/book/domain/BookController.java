package org.library.thelibraryj.book.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.*;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}/books")
record BookController(BookService bookService) implements ErrorHandling {

    @Operation(
            summary = "Retrieve all book previews with their tags",
            tags = "book"
    )
    @GetMapping
    public List<BookPreviewResponse> getBookPreviews() {
        return bookService.getBookPreviewResponses();
    }

    @Operation(
            summary = "Retrieve a book detail with ratings and chapter previews by book Id",
            tags = "book"
    )
    @GetMapping("/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id) {
        return handle(bookService.getBookDetailResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a book previews with its tags by book Id",
            tags = "book"
    )
    @GetMapping("/preview/{id}")
    public ResponseEntity<String> getBookPreview(@PathVariable UUID id) {
        return handle(bookService.getBookPreviewResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a book detail with ratings and chapter previews by book title",
            tags = "book"
    )
    @GetMapping("/book/{title}")
    public ResponseEntity<String> getBookDetailById(@PathVariable String title) {
        return handle(bookService.getBook(title), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new book entry",
            tags = "book"
    )
    @PostMapping("/book")
    public ResponseEntity<String> createBook(@RequestBody @Valid BookCreationRequest bookCreationRequest) {
        return handle(bookService.createBook(bookCreationRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create a new chapter entry",
            tags = "book"
    )
    @PostMapping("/book/chapter")
    public ResponseEntity<String> createChapter(@RequestBody @Valid ChapterRequest chapterRequest) {
        return handle(bookService.createChapter(chapterRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create new chapter entries in batch",
            tags = "book"
    )
    @PostMapping("/book/chapter/batch")
    public ResponseEntity<String> createChapters(@RequestBody @Valid List<ChapterRequest> chapterRequests) {
        return handle(bookService.createChapters(chapterRequests), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing book entry",
            tags = "book"
    )
    @PatchMapping("/book")
    public ResponseEntity<String> updateBook(@RequestBody @Valid BookUpdateRequest bookUpdateRequest) {
        return handle(bookService.updateBook(bookUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Create or update if already exists a new rating entry ",
            tags = "book"
    )
    @PutMapping("/rating")
    public ResponseEntity<String> upsertRating(@RequestBody @Valid RatingRequest ratingRequest) {
        return handle(bookService.upsertRating(ratingRequest), HttpStatus.OK);
    }


    @Operation(
            summary = "Reset the book previews cache. Cache resets automatically every 10 minutes",
            tags = "book"
    )
    @PutMapping("/flush")
    public ResponseEntity<String> flushPreviewsCache() {
        bookService.resetBookPreviewsCache();
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Remove a single chapter entry from a book",
            tags = "book"
    )
    @DeleteMapping("/book/chapter/{number}")
    public ResponseEntity<String> deleteChapter(@PathVariable Integer number, @RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteChapter(contentRemovalRequest, number), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a single book entry with all its associated chapters and ratings",
            tags = "book"
    )
    @DeleteMapping("/book")
    public ResponseEntity<String> deleteBook(@RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteBook(contentRemovalRequest), HttpStatus.OK);
    }

}
