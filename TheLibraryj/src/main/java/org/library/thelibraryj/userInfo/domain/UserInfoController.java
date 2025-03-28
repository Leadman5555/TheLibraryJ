package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile.ValidImageFormat;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.BookCollectionRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoPreferenceUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoStatusUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.UserPreferenceUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserProfileImageUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserRankUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserStatusUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${library.user.mapping}")
@Tag(name = "User", description = "User-related endpoints that require valid credentials to access - mostly related to changes with user profile and logged-in-user-specific actions.")
class UserInfoController implements ErrorHandling {

    private final UserInfoService userInfoService;

    @Operation(
            summary = "Forcibly updates user's rank (negative change means decreasing the rank)",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Rank updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserRankUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PatchMapping("/profile/rank/force")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> forceUpdateUserInfoRank(@RequestBody @Valid UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        return handle(userInfoService.forceUpdateRank(userInfoRankUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Updates user's rank if user score is sufficient",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Rank updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserRankUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "User not eligible for rank increase"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Authentication failure")
    })
    @PatchMapping("/profile/rank/{email}")
    public ResponseEntity<String> updateUserInfoRank(@PathVariable("email") @Email String email) {
        return handle(userInfoService.updateRank(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's username and reflect the change on all his books. Username has characters constraints: '^[a-zA-Z0-9_-]+$'",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Username updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserUsernameUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username not unique")
    })
    @PatchMapping("/profile/username")
    @PreAuthorize("hasRole('ADMIN') or #userInfoUsernameUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoUsername(@RequestBody @Valid UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        return handle(userInfoService.updateUserInfoUsername(userInfoUsernameUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's status",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Status updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserStatusUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/profile/status")
    @PreAuthorize("hasRole('ADMIN') or #userInfoStatusUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoStatus(@RequestBody @Valid UserInfoStatusUpdateRequest userInfoStatusUpdateRequest) {
        return handle(userInfoService.updateUserInfoStatus(userInfoStatusUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's preference",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Preference updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserPreferenceUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid preference or rank lacking"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/profile/preference")
    @PreAuthorize("#userInfoPreferenceUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoPreference(@RequestBody @Valid UserInfoPreferenceUpdateRequest userInfoPreferenceUpdateRequest) {
        return handle(userInfoService.updateUserInfoPreference(userInfoPreferenceUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's profile image for a new one. If none sent, replaces user profile image with the default one.",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Profile image updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileImageUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to save the update image on server"),
    })
    @PatchMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> updateUserProfileImage(@RequestParam("email") @Email String email,
                                                         @RequestPart(value = "newImage", required = false) @Nullable @ValidImageFormat MultipartFile newImage) throws IOException {
        return handle(userInfoService.updateProfileImage(new UserInfoImageUpdateRequest(email, newImage)), HttpStatus.OK);
    }


    @Operation(
            summary = "Fetch previews of books (or Ids if 'onlyIds' param is true) that are added to user's favourites.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ids fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UUID.class))
                    )
            ),
            @ApiResponse(responseCode = "200",
                    description = "Book previews fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookPreviewResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "200", description = "Previews or Ids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @GetMapping(value = "/book/favourite")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> getFavouriteBooksForUser(@RequestParam("email") @Email String email, @RequestParam(value = "onlyIds", required = false) @Nullable Boolean onlyIds) {
        if (onlyIds != null && onlyIds) return handle(userInfoService.getFavouriteBooksIds(email), HttpStatus.OK);
        return handle(userInfoService.getFavouriteBooks(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Add a book to user's favourites. Returns current favourite book count on success.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found or book not found"),
    })
    @PostMapping(value = "/book/favourite")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> addBookToFavouritesForUser(@RequestParam("email") @Email String email,
                                                             @RequestParam("bookId") @NotNull UUID bookId) {
        return handle(userInfoService.addBookToFavourites(new BookCollectionRequest(email, bookId)), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a book from user's favourites.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book removed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found or book not found"),
    })
    @DeleteMapping(value = "/book/favourite")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> removeBookFromFavouritesForUser(@RequestParam("email") @Email String email,
                                                                  @RequestParam("bookId") @NotNull UUID bookId) {
        userInfoService.removeBookFromFavourites(new BookCollectionRequest(email, bookId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Fetch previews of books (or Ids if 'onlyIds' param is true) that user is subscribed to.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ids fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UUID.class))
                    )
            ),
            @ApiResponse(responseCode = "200",
                    description = "Book previews fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BookPreviewResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "200", description = "Previews or Ids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @GetMapping(value = "/book/subscribed")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> getSubscribedBooksForUser(@RequestParam("email") @Email String email, @RequestParam(value = "onlyIds", required = false) @Nullable Boolean onlyIds) {
        if (onlyIds != null && onlyIds) return handle(userInfoService.getSubscribedBooksIds(email), HttpStatus.OK);
        return handle(userInfoService.getSubscribedBooks(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Add a book to user's subscribed books. Returns current subscribed book count on success.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found or book not found"),
    })
    @PostMapping(value = "/book/subscribed")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> addBookToSubscribedForUser(@RequestParam("email") @Email String email,
                                                             @RequestParam("bookId") @NotNull UUID bookId) {
        return handle(userInfoService.addBookToSubscribed(new BookCollectionRequest(email, bookId)), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a book from user's subscribed books.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book removed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found or book not found"),
    })
    @DeleteMapping(value = "/book/subscribed")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> removeBookFromSubscribedForUser(@RequestParam("email") @Email String email,
                                                                  @RequestParam("bookId") @NotNull UUID bookId) {
        userInfoService.removeBookFromSubscribed(new BookCollectionRequest(email, bookId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Verify is the user can author books.",
            tags = {"user", "book"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User is eligible to author books."),
            @ApiResponse(responseCode = "403", description = "User is not eligible to author books")
    })
    @PostMapping("/verify/{email}")
    public ResponseEntity<String> verifyWritingEligibility(@PathVariable("email") @NotNull @Email String email) {
        if (userInfoService.checkWritingEligibility(email)) return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
