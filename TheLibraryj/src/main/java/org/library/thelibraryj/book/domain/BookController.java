package org.library.thelibraryj.book.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.BookCreationRequest;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookUpdateRequest;
import org.library.thelibraryj.book.dto.ChapterRequest;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}")
@RequiredArgsConstructor
class BookController implements ErrorHandling {
    private final BookService bookService;

    @Operation(
            summary = "Retrieve all book previews with their tags. Returns the asked for page",
            tags = {"book", "no auth required"}
    )
    @GetMapping("/na/books")
    public Page<BookPreviewResponse> getBookPreviews(@RequestParam(name = "page") int page) {
        return bookService.getBookPreviewResponsePage(page);
    }

    @Operation(
            summary = "Retrieve a book detail with ratings and chapter previews by book Id",
            tags = {"book", "no auth required"}
    )
    @GetMapping("/na/books/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id) {
        return handle(bookService.getBookDetailResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a book preview with its tags by book Id",
            tags = {"book", "no auth required"}
    )
    @GetMapping("na/books/preview/{id}")
    public ResponseEntity<String> getBookPreview(@PathVariable UUID id) {
        return handle(bookService.getBookPreviewResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a whole book by title",
            tags = {"book", "no auth required"}
    )
    @GetMapping("na/books/book/{title}")
    public ResponseEntity<String> getBookByTitle(@PathVariable String title) {
        return handle(bookService.getBook(title), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new book entry",
            tags = "book"
    )
    @PostMapping("books/book")
    @PreAuthorize("#bookCreationRequest.authorEmail == authentication.principal.username")
    public ResponseEntity<String> createBook(@RequestBody @Valid BookCreationRequest bookCreationRequest) {
        return handle(bookService.createBook(bookCreationRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create a new chapter entry",
            tags = "book"
    )
    @PostMapping("books/book/chapter")
    @PreAuthorize("#chapterRequest.authorEmail == authentication.principal.username")
    public ResponseEntity<String> createChapter(@RequestBody @Valid ChapterRequest chapterRequest) {
        return handle(bookService.createChapter(chapterRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create new chapter entries in batch",
            tags = "book"
    )
    @PostMapping("books/book/chapter/batch")
    @PreFilter("#filterObject.authorEmail == authentication.principal.username")
    public ResponseEntity<String> createChapters(@RequestBody @Valid List<ChapterRequest> chapterRequests) {
        return handle(bookService.createChapters(chapterRequests), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Fetch the content (text) of a single chapter by it's number and bookId",
            tags = {"book", "no auth required"}
    )
    @GetMapping("/na/books/book/chapter")
    public ResponseEntity<String> getBookChapter(@RequestParam("bookId") UUID bookId, @RequestParam("chapterNumber") int chapterNumber) {
        return handle(bookService.getChapterByBookIdAndNumber(bookId, chapterNumber), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing book entry",
            tags = "book"
    )
    @PatchMapping("books/book")
    @PreAuthorize("hasRole('ADMIN') or #bookUpdateRequest.authorEmail == authentication.principal.username")
    public ResponseEntity<String> updateBook(@RequestBody @Valid BookUpdateRequest bookUpdateRequest) {
        return handle(bookService.updateBook(bookUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Create or update if already exists a new rating entry ",
            tags = "book"
    )
    @PutMapping("books/rating")
    @PreAuthorize("hasRole('ADMIN') or #ratingRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> upsertRating(@RequestBody @Valid RatingRequest ratingRequest) {
        return handle(bookService.upsertRating(ratingRequest), HttpStatus.OK);
    }


    @Operation(
            summary = "Reset the book previews cache. Cache resets automatically every 10 minutes",
            tags = "book"
    )
    @PutMapping("books/flush")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> flushPreviewsCache() {
        bookService.resetBookPreviewsCache();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove a single chapter entry from a book",
            tags = "book"
    )
    @DeleteMapping("books/book/chapter/{number}")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteChapter(@PathVariable Integer number, @RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteChapter(contentRemovalRequest, number), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a single book entry with all its associated chapters and ratings",
            tags = "book"
    )
    @DeleteMapping("books/book")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteBook(@RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteBook(contentRemovalRequest), HttpStatus.OK);
    }

}
