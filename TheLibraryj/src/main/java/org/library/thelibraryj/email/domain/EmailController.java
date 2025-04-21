package org.library.thelibraryj.email.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.email.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("${library.email.mapping}")
@Tag(name = "Email", description = "Email endpoints that require admin privilege to access.")
class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "Tests connection with the email server",
            tags = "email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Can connect to the email server"
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "503", description = "Failed to connect to the email service")
    })
    @PostMapping(value = "/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> testEmailServerConnection() {
        if(emailService.testConnection())
            return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
