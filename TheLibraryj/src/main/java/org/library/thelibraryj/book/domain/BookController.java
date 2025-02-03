package org.library.thelibraryj.book.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.*;
import org.library.thelibraryj.book.dto.chapterDto.ChapterBatchRequest;
import org.library.thelibraryj.book.dto.pagingDto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedChapterPreviewResponse;
import org.library.thelibraryj.book.dto.pagingDto.PreviewKeySetPage;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.validators.batchSize.ValidBatchSize;
import org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile.ValidImageFormat;
import org.library.thelibraryj.infrastructure.validators.fileValidators.textFile.ValidTextFileFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}")
@RequiredArgsConstructor
class BookController implements ErrorHandling {
    private final BookService bookService;

    @Operation(
            summary = "Retrieve a page of book previews with their tags by offset navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the page of book previews"),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested page not found")
    })
    @GetMapping("/na/books")
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsPageByOffset(@RequestParam(name = "page") int page, @RequestParam(name = "pageSize") int pageSize) {
        return ResponseEntity.ok(bookService.getOffsetPagedBookPreviewResponses(pageSize, page));
    }

    @Operation(
            summary = "Retrieve a page of book previews with their tags by keySet navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the page of book previews"),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested page not found")
    })
    @PostMapping("/na/books")
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsPageByKeySet(@RequestParam(name = "page") int page, @NonNull @RequestBody PreviewKeySetPage keySetPage) {
        return ResponseEntity.ok(bookService.getKeySetPagedBookPreviewResponses(keySetPage, page));
    }

    @Operation(
            summary = "Retrieve all book previews with their tags that meet the given criteria",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book previews"),
    })
    @GetMapping("/na/books/filtered")
    public ResponseEntity<List<BookPreviewResponse>> getBookPreviewsByParams(@RequestParam(name = "titleLike", required = false) String titleLike,
                                                                             @RequestParam(name = "minChapters", required = false) Integer minChapters,
                                                                             @RequestParam(name = "minRating", required = false) Float minRating,
                                                                             @RequestParam(value = "state", required = false) BookState state,
                                                                             @RequestParam(value = "hasTags", required = false) BookTag[] hasTags,
                                                                             @RequestParam(value = "ratingOrder", required = false) Boolean ratingOrder) {
        return ResponseEntity.ok(bookService.getByParams(titleLike, minChapters, minRating, state, hasTags, ratingOrder));
    }

    @Operation(
            summary = "Retrieve all book previews authored by the given user",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book previews"),
    })
    @GetMapping("/na/books/authored/{byUser}")
    public ResponseEntity<List<BookPreviewResponse>> getBookPreviewsByAuthor(@PathVariable String byUser) {
        return ResponseEntity.ok(bookService.getBookPreviewsByAuthor(byUser));
    }

    @Operation(
            summary = "Retrieve a book detail by book Id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book with specified ID not found")
    })
    @GetMapping("/na/books/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id) {
        return handle(bookService.getBookDetailResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all ratings for book of given id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book ratings"),
            @ApiResponse(responseCode = "404", description = "Book with specified ID not found")
    })
    @GetMapping("/na/books/{id}/rating")
    public ResponseEntity<List<RatingResponse>> getBookRatings(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getRatingResponsesForBook(id));
    }


    @Operation(
            summary = "Retrieve a book preview with its tags by book Id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book preview retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book preview with specified ID not found")
    })
    @GetMapping("na/books/preview/{id}")
    public ResponseEntity<String> getBookPreview(@PathVariable UUID id) {
        return handle(bookService.getBookPreviewResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a whole book by title, chapter previews and ratings are not fetched",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book with specified title not found")
    })
    @GetMapping("na/books/book/{title}")
    public ResponseEntity<String> getBookByTitle(@PathVariable String title) {
        return handle(bookService.getBook(title), HttpStatus.OK);
    }

    @Operation(
            summary = "Create new chapter entries in batch. Batch size is limited to 50, distinct number entries. " +
                    "Chapters must be files in .doc, .docx, .txt or .odt format." +
                    " File name must be: 'CHAPTER_NUMBER - CHAPTER_TITLE.EXTENSION' or 'CHAPTER_NUMBER.EXTENSION'. Chapter title must meet required character constraints. Chapter length is limited.",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chapters created successfully"),
            @ApiResponse(responseCode = "400", description = "Request data invalid. More information in the return error."),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PutMapping(value = "books/book/{bookId}/chapter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreFilter("#authorEmail == authentication.principal.username")
    public ResponseEntity<String> upsertChapters(@RequestPart @ValidBatchSize @ValidTextFileFormat List<MultipartFile> chapterBatch,
                                                 @PathVariable("bookId") UUID bookId,
                                                 @RequestParam("authorEmail") @Email String authorEmail) {
        return handle(bookService.upsertChapters(new ChapterBatchRequest(chapterBatch, bookId, authorEmail)), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create a new book entry.",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PostMapping(value = "books/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#authorEmail == authentication.principal.username")
    public ResponseEntity<String> createBook(@ModelAttribute @Valid BookCreationModel bookCreationModel,
                                             @RequestPart(value = "coverImage", required = false) @ValidImageFormat MultipartFile coverImage,
                                             @RequestParam("authorEmail") @NotNull @Email String authorEmail) {
        return handle(bookService.createBook(new BookCreationRequest(bookCreationModel, coverImage, authorEmail)), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Fetch the content (text) of a single chapter by it's number and bookId",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chapter content"),
            @ApiResponse(responseCode = "404", description = "Request entities not found"),
    })
    @GetMapping("/na/books/book/chapter")
    public ResponseEntity<String> getBookChapter(@RequestParam("bookId") UUID bookId, @RequestParam("chapterNumber") int chapterNumber) {
        return handle(bookService.getChapterByBookIdAndNumber(bookId, chapterNumber), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a page of chapter previews by offset navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chapter previews"),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested chapter previews not found")
    })
    @GetMapping("/na/books/{id}/chapter")
    public ResponseEntity<PagedChapterPreviewResponse> getChapterPreviewPageByOffset(@RequestParam(name = "page") int page, @RequestParam(name = "pageSize") int pageSize, @PathVariable("id") UUID bookId) {
        return ResponseEntity.ok(bookService.getOffsetPagedChapterPreviewResponses(pageSize, page, bookId));
    }

    @Operation(
            summary = "Retrieve a page of chapter previews by keySet navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chapter previews"),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested chapter previews not found")
    })
    @PostMapping("/na/books/{id}/chapter")
    public ResponseEntity<PagedChapterPreviewResponse> getChapterPreviewPageByKeySet(@RequestParam(name = "page") int page, @NonNull @RequestBody PreviewKeySetPage keySetPage, @PathVariable("id") UUID bookId) {
        return ResponseEntity.ok(bookService.getKeySetPagedChapterPreviewResponses(keySetPage, page, bookId));
    }

    @Operation(
            summary = "Update an existing book entry.",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "409", description = "Duplicate title"),
    })
    @PutMapping(value = "books/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #authorEmail == authentication.principal.username")
    public ResponseEntity<String> updateBook(
            @ModelAttribute @Valid BookUpdateModel bookUpdateModel,
            @RequestPart(value = "coverImage", required = false) @Nullable MultipartFile coverImage,
            @RequestParam("bookId") @NotNull UUID bookId,
            @RequestParam("authorEmail") @NotNull String authorEmail
    ) {
        return handle(bookService.updateBook(new BookUpdateRequest(coverImage, bookUpdateModel, bookId, authorEmail)), HttpStatus.OK);
    }

    @Operation(
            summary = "Create or update if already exists a new rating entry ",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating entry upserted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PutMapping("books/rating")
    @PreAuthorize("hasRole('ADMIN') or #ratingRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> upsertRating(@RequestBody @Valid RatingRequest ratingRequest) {
        return handle(bookService.upsertRating(ratingRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Reset the book previews cache. Cache resets automatically every 10 minutes",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cache reset successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chapter deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Chapter to delete not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @DeleteMapping("books/book/chapter/{number}")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteChapter(@PathVariable @Min(1) Integer number, @RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteChapter(contentRemovalRequest, number), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a single book entry with all its associated chapters and ratings",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Book to delete not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @DeleteMapping("books/book")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteBook(@RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteBook(contentRemovalRequest), HttpStatus.OK);
    }

}
