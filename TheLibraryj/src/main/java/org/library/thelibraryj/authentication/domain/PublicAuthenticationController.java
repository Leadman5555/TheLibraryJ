package org.library.thelibraryj.authentication.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.dto.request.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.request.RegisterRequest;
import org.library.thelibraryj.authentication.dto.response.AuthenticationResponse;
import org.library.thelibraryj.authentication.userAuth.dto.response.UserCreationResponse;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile.ValidImageFormat;
import org.library.thelibraryj.infrastructure.validators.passwordCharacters.ValidPasswordCharacters;
import org.library.thelibraryj.infrastructure.validators.usernameCharacters.ValidUsernameCharacters;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("${library.servlet.auth_free_mapping}${library.auth.mapping}")
@Tag(name = "Authentication - Public", description = "Authentication endpoints that don't require valid credentials to access.")
class PublicAuthenticationController implements ErrorHandling {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Allows for creation of a new user account. Sends activation email on success.'",
            tags = {"authentication", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Account created, activation email sent.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters."),
            @ApiResponse(responseCode = "409", description = "Parts of user data required to be unique are not."),
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(@RequestPart("email") @Email String email,
                                           @RequestPart("password") @NotNull @NotEmpty @ValidPasswordCharacters String password,
                                           @RequestPart("username") @NotNull @Size(min = 5, max = 20) @ValidUsernameCharacters String username,
                                           @RequestPart(value = "profileImage", required = false) @ValidImageFormat MultipartFile profileImage) {
        return handle(authenticationService.register(new RegisterRequest(email, password.toCharArray(), username, profileImage)), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Returns a valid JWT token and refresh token as Http-only cookie on successful login attempt.",
            tags = {"authentication", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Log in successful, tokens sent as body, cookie added to response."),
            @ApiResponse(responseCode = "400", description = "Google account must login with Google authentication."),
            @ApiResponse(responseCode = "401", description = "Authentication failed."),
            @ApiResponse(responseCode = "404", description = "Account not found."),
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        Either<GeneralError, AuthenticationResponse> result = authenticationService.authenticate(authenticationRequest);
        if (result.isLeft()) return handleError(result);
        AuthenticationResponse success = result.get();
        response.addCookie(success.refreshToken());
        return handleSuccess(success.token(), HttpStatus.OK);
    }

    @Operation(
            summary = "Clears the refresh token cookie.",
            tags = {"authentication", "no auth required"}
    )
    @ApiResponse(responseCode = "204", description = "Empty refresh token cookie attached to response.")
    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        response.addCookie(authenticationService.clearRefreshToken());
    }

    @Operation(
            summary = "Resends an activation email for given email address on success. Creates new activation token fr the user",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Activation email reset, token created."),
            @ApiResponse(responseCode = "400", description = "User account already enabled."),
            @ApiResponse(responseCode = "404", description = "User account not found."),
    })
    @PostMapping("/activation")
    public ResponseEntity<String> resendActivationEmail(@RequestParam @NotNull @Email String email) {
        return authenticationService.resendActivationEmail(email).fold(
                this::handleError,
                _ -> ResponseEntity.noContent().build()
        );
    }

    @Operation(
            summary = "Sends back a new JWT token for user is refresh token is valid",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jwt token cookie refreshed and attached as cookie to response."),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid."),
            @ApiResponse(responseCode = "404", description = "Refresh token missing")
    })
    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> refreshJwtToken(HttpServletRequest request, HttpServletResponse response) {
        String newToken = authenticationService.regenerateAccessToken(request.getCookies());
        response.addHeader("access_token", newToken);
        return ResponseEntity.noContent().build();
    }
}
