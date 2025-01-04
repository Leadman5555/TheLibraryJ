package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> getUserInfoResponseById(@PathVariable("id") UUID id) {
        return handle(userInfoService.getUserInfoResponseById(id), HttpStatus.OK);
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
    public ResponseEntity<String> getUserInfoResponseByUsername(@PathVariable("username") String username) {
        return handle(userInfoService.getUserInfoResponseByUsername(username), HttpStatus.OK);
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
    public ResponseEntity<String> getUserInfoResponseByEmail(@PathVariable("email") String email) {
        return handle(userInfoService.getUserInfoResponseByEmail(email), HttpStatus.OK);
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
            summary = "Forcibly updates user's rank (negative change means decreasing the rank)",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rank updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Authentication failure")
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
            @ApiResponse(responseCode = "403", description = "Authentication failure")
    })
    @PatchMapping("/user/profile/rank/{id}")
    public ResponseEntity<String> updateUserInfoRank(@PathVariable("id") UUID id) {
        return handle(userInfoService.updateRank(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's username and reflect the change on all his books",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Authentication failure"),
            @ApiResponse(responseCode = "409", description = "Username not unique")
    })
    @PatchMapping("/user/profile/username")
    @PreAuthorize("hasRole('ADMIN') or #userInfoUsernameUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserInfoUsername(@RequestBody @Valid UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        return handle(userInfoService.updateUserInfoUsername(userInfoUsernameUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's profile image for a new one",
            tags = "user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Authentication failure"),
            @ApiResponse(responseCode = "500", description = "Failed to save the update image on server")
    })
    @PatchMapping("/user/profile/image")
    @PreAuthorize("hasRole('ADMIN') or #userInfoImageUpdateRequest.email == authentication.principal.username")
    public ResponseEntity<String> updateUserProfileImage(@RequestBody @Valid UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException {
        return handle(userInfoService.updateProfileImage(userInfoImageUpdateRequest), HttpStatus.OK);
    }
}
