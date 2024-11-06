package org.library.thelibraryj.authentication.tokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.library.thelibraryj.authentication.tokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${library.mapping}/auth/password")
record PasswordResetController(PasswordResetServiceImpl passwordResetService) implements ErrorHandling {

    @Operation(
            summary = "Starts the password reset procedure, creating a reset token and sending a password reset email to the user of given email address.",
            tags = {"authentication", "password"}
    )
    @PostMapping("/{emailAddress}")
    public ResponseEntity<String> startPasswordResetProcedure(@PathVariable String emailAddress) throws MessagingException {
        return handle(passwordResetService.startPasswordResetProcedure(emailAddress), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Consumes an existing password reset token, changing the user's password to the new one if successful.",
            tags = {"authentication", "password"}
    )
    @PatchMapping
    public ResponseEntity<String> consumePasswordResetToken(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        return handle(passwordResetService.consumePasswordResetToken(passwordResetRequest), HttpStatus.NO_CONTENT);
    }

}
