package org.library.thelibraryj.book.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.request.BookCreationModel;
import org.library.thelibraryj.book.dto.bookDto.request.BookCreationRequest;
import org.library.thelibraryj.book.dto.bookDto.request.BookUpdateModel;
import org.library.thelibraryj.book.dto.bookDto.request.BookUpdateRequest;
import org.library.thelibraryj.book.dto.bookDto.response.BookResponse;
import org.library.thelibraryj.book.dto.chapterDto.request.ChapterBatchRequest;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterUpsertResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.library.thelibraryj.book.dto.sharedDto.request.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.sharedDto.response.ContentRemovalSuccess;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.validators.batchSize.ValidBatchSize;
import org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile.ValidImageFormat;
import org.library.thelibraryj.infrastructure.validators.fileValidators.textFile.ValidTextFileFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${library.book.mapping}")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book endpoints that require valid credentials to access - mostly related to author activities.")
class BookController implements ErrorHandling {
    private final BookService bookService;



    @Operation(
            summary = "Create new chapter entries in batch. Batch size is limited to 50, distinct number entries. " +
                    "Chapters must be files in .doc, .docx, .txt or .odt format." +
                    " File name must be: 'CHAPTER_NUMBER - CHAPTER_TITLE.EXTENSION' or 'CHAPTER_NUMBER.EXTENSION' or 'CHAPTER_NUMBER - $CHAPTER_TITLE.EXTENSION'. Chapter title must meet required character constraints. Chapter length is limited. If a chapter title starts with a $, it will be treated as a spoiler title and obscured appropriately",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Chapters created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ChapterUpsertResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Request data invalid. More information in the return error."),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PutMapping(value = "/book/{bookId}/chapter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#authorEmail == authentication.principal.username")
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
            @ApiResponse(responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PostMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#authorEmail == authentication.principal.username")
    public ResponseEntity<String> createBook(@ModelAttribute @Valid BookCreationModel bookCreationModel,
                                             @RequestPart(value = "coverImage", required = false) @ValidImageFormat MultipartFile coverImage,
                                             @RequestParam("authorEmail") @NotNull @Email String authorEmail) {
        return handle(bookService.createBook(new BookCreationRequest(bookCreationModel, coverImage, authorEmail)), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing book entry.",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "409", description = "Duplicate title"),
    })
    @PutMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #authorEmail == authentication.principal.username")
    public ResponseEntity<String> updateBook(
            @ModelAttribute @Valid BookUpdateModel bookUpdateModel,
            @RequestPart(value = "coverImage", required = false) @Nullable @ValidImageFormat MultipartFile coverImage,
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
            @ApiResponse(responseCode = "200",
                    description = "Rating entry upserted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RatingResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Request entities or users not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PutMapping("/rating")
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
    @PutMapping("/flush")
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
            @ApiResponse(responseCode = "200",
                    description = "Chapter deleted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ContentRemovalSuccess.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Chapter to delete not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @DeleteMapping("/book/chapter/{number}")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteChapter(@PathVariable @Min(1) @Max(10000) Integer number, @RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteChapter(contentRemovalRequest, number), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a single book entry with all its associated chapters and ratings",
            tags = "book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Book deleted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ContentRemovalSuccess.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Book to delete not found"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @DeleteMapping("/book")
    @PreAuthorize("hasRole('ADMIN') or #contentRemovalRequest.userEmail == authentication.principal.username")
    public ResponseEntity<String> deleteBook(@RequestBody @Valid ContentRemovalRequest contentRemovalRequest) {
        return handle(bookService.deleteBook(contentRemovalRequest), HttpStatus.OK);
    }

}
