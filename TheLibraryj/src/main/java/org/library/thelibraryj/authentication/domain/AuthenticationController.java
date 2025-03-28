package org.library.thelibraryj.authentication.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("${library.auth.mapping}")
@Tag(name = "Authentication", description = "Authentication endpoints that require valid credentials to access.")
class AuthenticationController implements ErrorHandling {

    @SuppressWarnings("EmptyMethod")
    @Operation(
            summary = "Verify matching userData and JWT token.",
            tags = {"authentication"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Valid token."),
            @ApiResponse(responseCode = "401", description = "Authorization failure."),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/verify/{email}")
    @PreAuthorize("#email == authentication.principal.username")
    public void verify(@PathVariable @NotNull @Email String email) {}
}
