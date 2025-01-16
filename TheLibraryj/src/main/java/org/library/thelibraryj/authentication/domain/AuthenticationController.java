package org.library.thelibraryj.authentication.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("${library.mapping}")
class AuthenticationController implements ErrorHandling {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Allows for creation of a new user account. Sends activation email on success.",
            tags = {"authentication", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created, activation email sent."),
            @ApiResponse(responseCode = "409", description = "Parts of user data required to be unique are not."),
    })
    @PostMapping( value = "/na/auth/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(@RequestPart("email") @Email String email,
                                           @RequestPart("password") @NotNull @NotEmpty String password,
                                           @RequestPart("username") @NotNull @Size(min = 5, max = 20) String username,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws MessagingException {
        return handle(authenticationService.register(new RegisterRequest(email, password.toCharArray(), username, profileImage)), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Returns a valid JWT token and refresh token as Http-only cookie on successful login attempt.",
            tags = {"authentication", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Log in successful, tokens sent."),
            @ApiResponse(responseCode = "400", description = "Google account must login with Google authentication."),
            @ApiResponse(responseCode = "401", description = "Authentication failed."),
            @ApiResponse(responseCode = "404", description = "Account not found."),
    })
    @PostMapping("/na/auth/login")
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
    @GetMapping("/na/auth/logout")
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
    @PostMapping("/na/auth/activation")
    public ResponseEntity<String> resendActivationEmail(@RequestParam @NotNull @Email String email) throws MessagingException {
        return handle(authenticationService.resendActivationEmail(email), HttpStatus.NO_CONTENT);
    }

//    @Operation(
//            summary = "Obtains a new XSRF token as a cookie attached to response.",
//            tags = {"authentication", "no auth required"}
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "XSRF token cookie refreshed and attached as cookie to response."),
//            @ApiResponse(responseCode = "401", description = "New XSRF token generated and attached as cookie to response."),
//    })
//    @PostMapping("/auth/csrf")
//    public ResponseEntity<String> obtainCsrfToken() {
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @Operation(
            summary = "Sends back a new JWT token for user is refresh token is valid",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "XSRF token cookie refreshed and attached as cookie to response."),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid."),
            @ApiResponse(responseCode = "404", description = "Refresh token missing")
    })
    @GetMapping("/na/auth/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> refreshJwtToken(HttpServletRequest request, HttpServletResponse response) {
        String newToken = authenticationService.regenerateAccessToken(request.getCookies());
        response.addHeader("access_token", newToken);
        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("EmptyMethod")
    @Operation(
            summary = "Verify matching userData and JWT token.",
            tags = {"authentication"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Valid tokens."),
            @ApiResponse(responseCode = "401", description = "Authorization failure."),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/auth/verify/{email}")
    @PreAuthorize("#email == authentication.principal.username")
    public void verify(@PathVariable @NotNull @Email String email) {}
}
