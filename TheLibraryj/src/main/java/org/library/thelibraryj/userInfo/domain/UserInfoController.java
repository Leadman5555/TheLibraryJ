package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.*;
import org.library.thelibraryj.userInfo.dto.response.UserInfoMiniResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}")
@RequiredArgsConstructor
class UserInfoController implements ErrorHandling {

    private final UserInfoService userInfoService;

    @Operation(
            summary = "Fetch a single UserInfo record by Id",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/na/user/id/{id}")
    public ResponseEntity<String> getUserProfileById(@PathVariable("id") UUID id) {
        return handle(userInfoService.getUserProfileById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch a single UserInfo record by username",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/na/user/{username}")
    public ResponseEntity<String> getUserProfileByUsername(@PathVariable("username") String username) {
        return handle(userInfoService.getUserProfileByUsername(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch a single UserInfo record by email",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/na/user/email/{email}")
    public ResponseEntity<String> getUserProfileByEmail(@PathVariable("email") String email) {
        return handle(userInfoService.getUserProfileByEmail(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch extra details of UserInfo record by username",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/na/user/details/{username}")
    public ResponseEntity<UserInfoDetailsView> getUserInfoDetailsByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userInfoService.getUserInfoDetailsByUsername(username));
    }

    @Operation(
            summary = "Fetch basic information of UserInfo record by email",
            tags = {"user", "no auth required"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/na/user/mini/{email}")
    public ResponseEntity<UserInfoMiniResponse> getUserInfoMiniByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(userInfoService.getUserInfoMiniResponseByEmail(email));
    }

    @Operation(
            summary = "Forcibly updates user's rank (negative change means decreasing the rank)",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rank updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking")
    })
    @PatchMapping("/user/profile/rank/force")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> forceUpdateUserInfoRank(@RequestBody @Valid UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        return handle(userInfoService.forceUpdateRank(userInfoRankUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Updates user's rank if user score is sufficient",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rank updated successfully"),
            @ApiResponse(responseCode = "400", description = "User not eligible for rank increase"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Authentication failure")
    })
    @PatchMapping("/user/profile/rank/{email}")
    public ResponseEntity<String> updateUserInfoRank(@PathVariable("email") @Email String email) {
        return handle(userInfoService.updateRank(email), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's username and reflect the change on all his books. Username has characters constraints: '^[a-zA-Z0-9_-]+$'",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request data invalid"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username not unique")
    })
    @PatchMapping("/user/profile/username")
    @PreAuthorize("hasRole('ADMIN') or #userInfoUsernameUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoUsername(@RequestBody @Valid UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        return handle(userInfoService.updateUserInfoUsername(userInfoUsernameUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's status",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/user/profile/status")
    @PreAuthorize("hasRole('ADMIN') or #userInfoStatusUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoStatus(@RequestBody @Valid UserInfoStatusUpdateRequest userInfoStatusUpdateRequest) {
        return handle(userInfoService.updateUserInfoStatus(userInfoStatusUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's preference",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid preference or rank lacking"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/user/profile/preference")
    @PreAuthorize("#userInfoPreferenceUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoPreference(@RequestBody @Valid UserInfoPreferenceUpdateRequest userInfoPreferenceUpdateRequest) {
        return handle(userInfoService.updateUserInfoPreference(userInfoPreferenceUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's profile image for a new one. If none sent, replaces user profile image with the default one.",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failure"),
            @ApiResponse(responseCode = "403", description = "Permission lacking"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to save the update image on server"),
    })
    @PatchMapping(value = "/user/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<String> updateUserProfileImage(@RequestPart("email") @NotBlank @Email String email,
                                                         @RequestPart(value = "newImage", required = false) @Nullable MultipartFile newImage) throws IOException {
        return handle(userInfoService.updateProfileImage(new UserInfoImageUpdateRequest(email, newImage)), HttpStatus.OK);
    }

    @Operation(
            summary = "Verify is the user can author books.",
            tags = {"user", "book"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User is eligible to author books."),
            @ApiResponse(responseCode = "403", description = "User is not eligible to author books")
    })
    @PostMapping("/na/user/verify/{email}")
    public ResponseEntity<String> verifyWritingEligibility(@PathVariable @NotNull @Email String email) {
        if(userInfoService.checkWritingEligibility(email)) return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
