package org.library.thelibraryj.authentication.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${library.mapping}")
record AuthenticationController(AuthenticationService authenticationService) implements ErrorHandling {

    @Operation(
            summary = "Allows for creation of a new user account. Sends activation email on success.",
            tags = {"authentication", "no auth required"}
    )
    @PostMapping("/na/auth/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) throws MessagingException {
        return handle(authenticationService.register(registerRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Returns a valid JWT token on successful login attempt.",
            tags = {"authentication", "no auth required"}
    )
    @PostMapping("/na/auth/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        Either<GeneralError, AuthenticationResponse> result = authenticationService.authenticate(authenticationRequest);
        if(result.isLeft()) return handleError(result);
        AuthenticationResponse success = result.get();
        response.addCookie(success.refreshToken());
        return handleSuccess(success.token(), HttpStatus.OK);
    }

    @Operation(
            summary = "Clears the refresh token cookie.",
            tags = {"authentication", "no auth required"}
    )
    @GetMapping("/na/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        response.addCookie(authenticationService.clearRefreshToken());
    }

    @Operation(
            summary = "Resends an activation email for given email address on success.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @PostMapping("/na/auth/activation")
    public ResponseEntity<String> resendActivationEmail(@RequestParam @NotNull @Email String email) throws MessagingException {
        return handle(authenticationService.resendActivationEmail(email), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Obtains the XSRF token cookie.",
            tags = {"authentication", "no auth required"}
    )
    @PostMapping("/auth/csrf")
    public ResponseEntity<String> obtainCsrfToken() {return new ResponseEntity<>(HttpStatus.NO_CONTENT);}
}
