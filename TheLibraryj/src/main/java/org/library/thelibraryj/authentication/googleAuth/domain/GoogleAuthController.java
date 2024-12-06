package org.library.thelibraryj.authentication.googleAuth.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
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
            tags = {"authentication", "google"}
    )
    @GetMapping
    public ResponseEntity<String> getGoogleAuthUrl(){
        return new ResponseEntity<>(googleAuthService.getGoogleAuthUrl(), HttpStatus.OK);
    }

    @Operation(
            summary = "Callback from the google login procedure. Creates a new user account if not already existing, returns a valid JWT token on success.",
            tags = {"authentication", "google"}
    )
    @GetMapping("callback")
    public ResponseEntity<String> getGoogleAuthCallbackUrl(@RequestParam @NotNull String code) {
        return handle(googleAuthService.getGoogleAuthToken(code), HttpStatus.OK);
    }
}
