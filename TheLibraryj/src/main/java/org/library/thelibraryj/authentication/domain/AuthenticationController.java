package org.library.thelibraryj.authentication.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${library.mapping}/na/auth")
record AuthenticationController(AuthenticationService authenticationService) implements ErrorHandling {

    @Operation(
            summary = "Allows for creation of a new user account. Sends activation email on success.",
            tags = {"authentication", "no auth required"}
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) throws MessagingException {
        return handle(authenticationService.register(registerRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Returns a valid JWT token on successful login attempt.",
            tags = {"authentication", "no auth required"}
    )
    @PostMapping
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return handle(authenticationService.authenticate(authenticationRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Resends an activation email for given email address on success.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @PostMapping("/activation")
    public ResponseEntity<String> resendActivationEmail(@RequestParam @NotNull @Email String email) throws MessagingException {
        return handle(authenticationService.resendActivationEmail(email), HttpStatus.NO_CONTENT);
    }
}
