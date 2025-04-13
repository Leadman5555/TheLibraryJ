package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.userInfoTokenServices.UserInfoTokenService;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.response.BookTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("${library.user.mapping}/book/token")
@Tag(name = "User - Token Services", description = "User-related endpoints that don't require valid credentials to access - mostly related to various tokens users can generate and use.")
class UserInfoTokenServicesController implements ErrorHandling {

    private final UserInfoTokenService userInfoTokenService;

    @Operation(
            summary = "Generate a new one or return an existing valid Favourite Book token.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Token returned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookTokenResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "201", description = "Token created and returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or #bookTokenRequest.email == authentication.principal.username")
    public ResponseEntity<String> upsertAndGetFavouriteBookToken(@RequestBody @Valid BookTokenRequest bookTokenRequest) {
        return userInfoTokenService.upsertFavouriteBookToken(bookTokenRequest).fold(
                this::handleError,
                success -> handleSuccess(success, success.justCreated() ? HttpStatus.CREATED : HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Add favourite books from the token owner to the requesting user's favourite books. Increases the token use count by one.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Books merged successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FavouriteBookMergerResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Token expired"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Token or user not found"),
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or #bookTokenConsummationRequest.email == authentication.principal.username")
    public ResponseEntity<String> mergeFavouriteBooksUsingToken(@RequestBody @Valid BookTokenConsummationRequest bookTokenConsummationRequest) {
        return handle(userInfoTokenService.consumeFavouriteBookToken(bookTokenConsummationRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Send an existing token to it's owner's email. Does not create a new token in case of an invalid one.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token sent to email"),
            @ApiResponse(responseCode = "400", description = "Token expired"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Token or user not found"),
    })
    @PostMapping(value = "/email")
    @PreAuthorize("hasRole('ADMIN') or #bookTokenConsummationRequest.email == authentication.principal.username")
    public ResponseEntity<String> sendExistingFavouriteBookTokenToOwnerEmail(@RequestBody @Valid BookTokenConsummationRequest bookTokenConsummationRequest) {
        return userInfoTokenService.sendTokenToEmail(bookTokenConsummationRequest).fold(
                this::handleError,
                any -> ResponseEntity.noContent().build()
        );
    }
}
