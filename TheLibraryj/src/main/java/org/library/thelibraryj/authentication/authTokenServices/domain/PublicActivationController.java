package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication - Public - Activation", description = "Authentication endpoints don't that require valid credentials to access and are related to account activation process.")
record PublicActivationController(ActivationTokenService activationTokenService) implements ErrorHandling {

    @Operation(
            summary = "Enable an account by consuming an already existing account activation token.",
            tags = {"authentication", "activation", "no auth required"},
            parameters = {@Parameter(name = "tokenId", description = "Token value to be consumed.", required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activation token successfully consumed, account enabled."),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token or other issues."),
            @ApiResponse(responseCode = "404", description = "Not Found - Token does not exist.")
    })
    @PatchMapping
    public ResponseEntity<String> consumeActivationToken(@RequestParam("token") @NotNull UUID token) {
        return activationTokenService.consumeActivationToken(token).fold(
                this::handleError,
                _ -> ResponseEntity.noContent().build()
        );
    }
}
