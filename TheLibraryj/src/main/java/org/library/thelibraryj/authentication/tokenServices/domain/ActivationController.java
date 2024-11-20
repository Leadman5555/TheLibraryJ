package org.library.thelibraryj.authentication.tokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.authentication.tokenServices.ActivationService;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
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
    @PatchMapping("/{tokenId}")
    public ResponseEntity<String> consumeActivationToken(@PathVariable("tokenId") UUID tokenId) {
        return handle(activationService.consumeActivationToken(tokenId), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Deletes all activation token records from the database that are already used and/or expired.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllUsedAndExpiredTokens() {
        activationService.deleteAllUsedAndExpiredTokens();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ActivationService activationService() {
        return activationService;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ActivationController) obj;
        return Objects.equals(this.activationService, that.activationService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activationService);
    }

    @Override
    public String toString() {
        return "ActivationController[" +
                "activationService=" + activationService + ']';
    }


}
