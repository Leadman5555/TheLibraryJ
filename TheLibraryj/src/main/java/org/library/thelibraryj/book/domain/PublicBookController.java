package org.library.thelibraryj.book.domain;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.response.BookDetailResponse;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.book.dto.bookDto.response.BookResponse;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedBookPreviewsResponse;
import org.library.thelibraryj.book.dto.pagingDto.PagedChapterPreviewResponse;
import org.library.thelibraryj.book.dto.pagingDto.PreviewKeySetPage;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${library.servlet.auth_free_mapping}${library.book.mapping}")
@Tag(name = "Book - Public", description = "Book endpoints that require don't valid credentials to access.")
class PublicBookController implements ErrorHandling {

    private final BookService bookService;

    @Operation(
            summary = "Retrieve a page of book previews with their tags by offset navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the page of book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedBookPreviewsResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested page not found")
    })
    @GetMapping
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsPageByOffset(@RequestParam(name = "pageSize") @Min(1) int pageSize,
                                                                                 @RequestParam(name = "page") @Min(0) int page) {
        return ResponseEntity.ok(bookService.getOffsetPagedBookPreviewResponses(pageSize, page));
    }

    @Operation(
            summary = "Retrieve a page of book previews with their tags by keySet navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the page of book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedBookPreviewsResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested page not found")
    })
    @PostMapping
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsPageByKeySet(@RequestParam(name = "page") @Min(0) int page,
                                                                                 @NonNull @RequestBody PreviewKeySetPage keySetPage) {
        return ResponseEntity.ok(bookService.getKeySetPagedBookPreviewResponses(keySetPage, page));
    }

    @Operation(
            summary = "Retrieve all book previews with their tags that meet the given criteria paged by offset",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the page of book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedBookPreviewsResponse.class)
                    )
            ),
    })
    @GetMapping("/filtered")
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsByParamsPageByOffset(@RequestParam(name = "titleLike", required = false) String titleLike,
                                                                                         @RequestParam(name = "minChapters", required = false) Integer minChapters,
                                                                                         @RequestParam(name = "minRating", required = false) Float minRating,
                                                                                         @RequestParam(value = "state", required = false) BookState state,
                                                                                         @RequestParam(value = "hasTags", required = false) BookTag[] hasTags,
                                                                                         @RequestParam(value = "sortAscByRating", required = false) Boolean sortAscByRating,
                                                                                         @RequestParam(name = "pageSize") @Min(1) int pageSize,
                                                                                         @RequestParam(name = "page") @Min(0) int page) {
        return ResponseEntity.ok(bookService.getByParamsOffsetPaged(titleLike, minChapters, minRating, state, hasTags, sortAscByRating, pageSize, page));
    }

    @Operation(
            summary = "Retrieve all book previews with their tags that meet the given criteria paged by keyset",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the page of book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedBookPreviewsResponse.class)
                    )
            ),
    })
    @PostMapping("/filtered")
    public ResponseEntity<PagedBookPreviewsResponse> getBookPreviewsByParamsPageByKeySet(@RequestParam(name = "titleLike", required = false) String titleLike,
                                                                                         @RequestParam(name = "minChapters", required = false) Integer minChapters,
                                                                                         @RequestParam(name = "minRating", required = false) Float minRating,
                                                                                         @RequestParam(value = "state", required = false) BookState state,
                                                                                         @RequestParam(value = "hasTags", required = false) BookTag[] hasTags,
                                                                                         @RequestParam(value = "sortAscByRating", required = false) Boolean sortAscByRating,
                                                                                         @RequestParam(name = "page") @Min(0) int page,
                                                                                         @NonNull @RequestBody PreviewKeySetPage keySetPage) {
        return ResponseEntity.ok(bookService.getByParamsKeySetPaged(titleLike, minChapters, minRating, state, hasTags, sortAscByRating, keySetPage, page));
    }

    @Operation(
            summary = "Retrieve all book previews authored by the given user",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookPreviewResponse.class))
                    )
            ),
    })
    @GetMapping("/authored/{byUser}")
    public ResponseEntity<List<BookPreviewResponse>> getBookPreviewsByAuthor(@PathVariable String byUser) {
        return ResponseEntity.ok(bookService.getBookPreviewsByAuthor(byUser));
    }

    @Operation(
            summary = "Retrieve all book previews that match given Ids. POST request to send ids through body instead of params.",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the book previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookPreviewResponse.class))
                    )
            ),
    })
    @PostMapping("/id")
    public ResponseEntity<List<BookPreviewResponse>> getBookPreviewsById(@RequestBody @NotEmpty Set<UUID> bookIds) {
        return ResponseEntity.ok(bookService.getBookPreviewsByIds(bookIds));
    }

    @Operation(
            summary = "Retrieve a book detail by book Id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the book detail",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                           schema = @Schema(implementation = BookDetailResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Book with specified ID not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<String> getBookDetail(@PathVariable UUID id) {
        return handle(bookService.getBookDetailResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all ratings for book of given id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved book ratings",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = RatingResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Book with specified ID not found")
    })
    @GetMapping("/{id}/rating")
    public ResponseEntity<List<RatingResponse>> getBookRatings(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getRatingResponsesForBook(id));
    }


    @Operation(
            summary = "Retrieve a book preview with its tags by book Id",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the book preview with its tags",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookPreviewResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Book preview with specified ID not found")
    })
    @GetMapping("/preview/{id}")
    public ResponseEntity<String> getBookPreview(@PathVariable UUID id) {
        return handle(bookService.getBookPreviewResponse(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a whole book by title, chapter previews and ratings are not fetched",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the book",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Book with specified title not found")
    })
    @GetMapping("/book/{title}")
    public ResponseEntity<String> getBookByTitle(@PathVariable String title) {
        return handle(bookService.getBook(title), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the content (text) of a single chapter by it's number and bookId",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved chapter content",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChapterResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Request entities not found"),
    })
    @GetMapping("/book/chapter")
    public ResponseEntity<String> getBookChapter(@RequestParam("bookId") UUID bookId, @RequestParam("chapterNumber") int chapterNumber) {
        return handle(bookService.getChapterByBookIdAndNumber(bookId, chapterNumber), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve a page of chapter previews by offset navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved paged chapter previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedChapterPreviewResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested chapter previews not found")
    })
    @GetMapping("/{id}/chapter")
    public ResponseEntity<PagedChapterPreviewResponse> getChapterPreviewPageByOffset(@RequestParam(name = "page") int page, @RequestParam(name = "pageSize") int pageSize, @PathVariable("id") UUID bookId) {
        return ResponseEntity.ok(bookService.getOffsetPagedChapterPreviewResponses(pageSize, page, bookId));
    }

    @Operation(
            summary = "Retrieve a page of chapter previews by keySet navigation. Returns the asked for page and current keySet",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved paged chapter previews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedChapterPreviewResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid paging data provided"),
            @ApiResponse(responseCode = "404", description = "Requested chapter previews not found")
    })
    @PostMapping("/{id}/chapter")
    public ResponseEntity<PagedChapterPreviewResponse> getChapterPreviewPageByKeySet(@RequestParam(name = "page") int page, @NonNull @RequestBody PreviewKeySetPage keySetPage, @PathVariable("id") UUID bookId) {
        return ResponseEntity.ok(bookService.getKeySetPagedChapterPreviewResponses(keySetPage, page, bookId));
    }
}
