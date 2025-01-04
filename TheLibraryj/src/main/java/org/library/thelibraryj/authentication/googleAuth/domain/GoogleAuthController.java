package org.library.thelibraryj.authentication.googleAuth.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponse;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponseWrapper;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleLinkResponse;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${library.mapping}/na/auth/google")
record GoogleAuthController(GoogleAuthService googleAuthService) implements ErrorHandling {
    @Operation(
            summary = "Request a google authentication link to login with a google account.",
            tags = {"authentication", "google", "no auth required"}
    )
    @ApiResponse(responseCode = "200", description = "Google authentication link sent in response body.")
    @GetMapping
    public ResponseEntity<GoogleLinkResponse> getGoogleAuthUrl(){
        return new ResponseEntity<>(googleAuthService.getGoogleAuthUrl(), HttpStatus.OK);
    }

    @Operation(
            summary = "Callback from the google login procedure. Creates a new user account if not already existing, returns a valid JWT token and refresh token as cookie on success.",
            tags = {"authentication", "google", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created, tokens sent."),
            @ApiResponse(responseCode = "403", description = "Verification error."),
            @ApiResponse(responseCode = "503", description = "Google authentication service not available.")
    })
    @GetMapping("callback")
    public ResponseEntity<GoogleCallbackResponse> getGoogleAuthCallbackUrl(@RequestParam @NotNull String code, HttpServletResponse response) {
        GoogleCallbackResponseWrapper callbackWrapper = googleAuthService.getGoogleAuthToken(code);
        response.addCookie(callbackWrapper.refreshToken());
        return new ResponseEntity<>(callbackWrapper.googleCallbackResponse(), HttpStatus.OK);
    }
}
