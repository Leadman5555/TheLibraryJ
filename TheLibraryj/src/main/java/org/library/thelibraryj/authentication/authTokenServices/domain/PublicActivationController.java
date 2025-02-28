package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.authentication.authTokenServices.ActivationTokenService;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("${library.servlet.auth_free_mapping}${library.auth.mapping}/activation")
record PublicActivationController(ActivationTokenService activationTokenService) implements ErrorHandling {

    @Operation(
            summary = "Enable an account by consuming an already existing account activation token.",
            tags = {"authentication", "activation", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activation token successfully consumed, account enabled."),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token or other issues."),
            @ApiResponse(responseCode = "404", description = "Not Found - Token does not exist.")
    })
    @PatchMapping
    public ResponseEntity<String> consumeActivationToken(@RequestParam("tokenId") @NotNull UUID tokenId) {
        return activationTokenService.consumeActivationToken(tokenId).fold(
                this::handleError,
                _ -> ResponseEntity.noContent().build()
        );
    }
}
