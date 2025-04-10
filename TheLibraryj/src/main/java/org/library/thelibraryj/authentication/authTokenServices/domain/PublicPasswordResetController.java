package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.library.thelibraryj.authentication.authTokenServices.PasswordResetTokenService;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${library.servlet.auth_free_mapping}${library.auth.mapping}/password")
@Tag(name = "Authentication - Public - Password", description = "Authentication endpoints that don't require valid credentials to access and are password related.")
record PublicPasswordResetController(PasswordResetTokenService passwordResetTokenService) implements ErrorHandling {

    @Operation(
            summary = "Starts the password reset procedure, creating a reset token and sending a password reset email to the user of given email address.",
            tags = {"authentication", "password", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset email sent successfully."),
            @ApiResponse(responseCode = "400", description = "Account not eligible for password reset."),
            @ApiResponse(responseCode = "404", description = "Account not found."),
    })
    @PostMapping("/{email}")
    public ResponseEntity<String> startPasswordResetProcedure(@PathVariable @Email String email) {
        return passwordResetTokenService.startPasswordResetProcedure(email).fold(
                this::handleError,
                any -> ResponseEntity.noContent().build()
        );
    }

    @Operation(
            summary = "Consumes an existing password reset token, changing the user's password to the new one if successful. ",
            tags = {"authentication", "password", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password reset token successfully consumed, password changed."),
            @ApiResponse(responseCode = "400", description = "Invalid token, invalid request data or other issues."),
            @ApiResponse(responseCode = "404", description = "Token does not exist.")
    })
    @PatchMapping
    public ResponseEntity<String> consumePasswordResetToken(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        return passwordResetTokenService.consumePasswordResetToken(passwordResetRequest).fold(
                this::handleError,
                any -> ResponseEntity.noContent().build()
        );
    }

}