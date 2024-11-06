package org.library.thelibraryj.authentication.activation.domain;

import io.swagger.v3.oas.annotations.Operation;
import org.library.thelibraryj.authentication.activation.ActivationService;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}/auth/activation")
record ActivationController(ActivationService activationService) implements ErrorHandling {
    @Operation(
            summary = "Enable an account by consuming an already existing account activation token.",
            tags = "authentication"
    )
    @PutMapping("/{tokenId}")
    public ResponseEntity<String> consumeActivationToken(@PathVariable("tokenId") UUID tokenId) {
        return handle(activationService.useActivationToken(tokenId), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Deletes all activation token records from the database that are already used and/or expired.",
            tags = "authentication"
    )
    @DeleteMapping
    public ResponseEntity<String> deleteAllUsedAndExpiredTokens() {
        activationService.deleteAllUsedAndExpiredTokens();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
