package org.library.thelibraryj.authentication.tokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.authentication.tokenServices.ActivationService;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${library.mapping}/na/auth/activation")
class ActivationController implements ErrorHandling {
    private final ActivationService activationService;

    @Operation(
            summary = "Enable an account by consuming an already existing account activation token.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activation token successfully consumed, account enabled."),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token or other issues."),
            @ApiResponse(responseCode = "404", description = "Not Found - Token does not exist.")
    })
    @PatchMapping("/{tokenId}")
    public ResponseEntity<String> consumeActivationToken(@PathVariable("tokenId") UUID tokenId) {
        return handle(activationService.consumeActivationToken(tokenId), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Deletes all activation token records from the database that are already used and/or expired.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponse(responseCode = "204", description = "Used and expired tokens deleted from the database.")
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllUsedAndExpiredTokens() {
        activationService.deleteAllUsedAndExpiredTokens();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
