package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.userInfoTokenServices.UserInfoTokenService;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${library.mapping}")
@RequiredArgsConstructor
public class UserInfoTokenServicesController implements ErrorHandling {

    private UserInfoTokenService userInfoTokenService;

    @Operation(
            summary = "Generate a new one or return an existing valid Favourite book token.",
            tags = {"book", "user"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token returned successfully"),
            @ApiResponse(responseCode = "201", description = "Token created and returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @PutMapping(value = "/user/book/token")
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
            @ApiResponse(responseCode = "200", description = "Books merged successfully"),
            @ApiResponse(responseCode = "400", description = "Token expired"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "404", description = "Token or user not found"),
    })
    @PostMapping(value = "/user/book/token")
    @PreAuthorize("hasRole('ADMIN') or #bookTokenConsummationRequest.email == authentication.principal.username")
    public ResponseEntity<String> mergeFavouriteBooksUsingToken(@RequestBody @Valid BookTokenConsummationRequest bookTokenConsummationRequest) {
        return handle(userInfoTokenService.consumeFavouriteBookToken(bookTokenConsummationRequest), HttpStatus.OK);
    }
}
