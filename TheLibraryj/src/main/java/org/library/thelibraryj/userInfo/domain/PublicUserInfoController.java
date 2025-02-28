package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.response.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${library.servlet.auth_free_mapping}${library.user.mapping}")
@Tag(name = "User - Public", description = "User-related endpoints that don't require valid credentials to access - mostly related to fetching publicly available information about users.")
class PublicUserInfoController implements ErrorHandling {

    private final UserInfoService userInfoService;

    @Operation(
            summary = "Fetch the profile of a user by userId",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved user profile",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<String> getUserProfileById(@PathVariable("id") UUID id) {
        return handle(userInfoService.getUserProfileById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the profile of a user by username",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved user profile",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<String> getUserProfileByUsername(@PathVariable("username") @NotBlank String username) {
        return handle(userInfoService.getUserProfileByUsername(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the profile of a user by email",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved user profile",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<String> getUserProfileByEmail(@PathVariable("email") @Email String email) {
        return handle(userInfoService.getUserProfileByEmail(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch extra details of a user by username",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved user details",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserInfoDetailsView.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/details/{username}")
    public ResponseEntity<String> getUserInfoDetailsByUsername(@PathVariable("username") @NotBlank String username) {
        return handle(userInfoService.getUserInfoDetailsByUsername(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch basic information of a user by email",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved basic user information",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserInfoMiniView.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/mini/{email}")
    public ResponseEntity<String> getUserInfoMiniByEmail(@PathVariable("email") @Email String email) {
        return handle(userInfoService.getUserInfoMiniResponseByEmail(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Verify is the user can author books.",
            tags = {"user", "book"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User is eligible to author books."),
            @ApiResponse(responseCode = "403", description = "User is not eligible to author books")
    })
    @PostMapping("/verify/{email}")
    public ResponseEntity<String> verifyWritingEligibility(@PathVariable("email") @NotNull @Email String email) {
        if (userInfoService.checkWritingEligibility(email)) return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
