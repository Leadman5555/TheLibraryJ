package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}")
record UserInfoController(UserInfoService userInfoService) implements ErrorHandling {


    @Operation(
            summary = "Fetch a single UserInfo record by Id",
            tags = {"user", "no auth required"}
    )
    @GetMapping("na/user/{id}")
    public ResponseEntity<String> getUserInfoResponseById(@PathVariable("id") UUID id) {
        return handle(userInfoService.getUserInfoResponseById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Forcibly updates user's rank (negative change means decreasing the rank)",
            tags = "user"
    )
    @PatchMapping("/user/profile/rank/force")
    public ResponseEntity<String> forceUpdateUserInfoRank(@RequestBody @Valid UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        return handle(userInfoService.forceUpdateRank(userInfoRankUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Updates user's rank if user score is sufficient",
            tags = "user"
    )
    @PatchMapping("/user/profile/rank/{id}")
    public ResponseEntity<String> updateUserInfoRank(@PathVariable("id") UUID id) {
        return handle(userInfoService.updateRank(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's username and reflect the change on all his books",
            tags = "user"
    )
    @PatchMapping("/user/profile/username")
    public ResponseEntity<String> updateUserInfoUsername(@RequestBody @Valid UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        return handle(userInfoService.updateUserInfoUsername(userInfoUsernameUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's profile image for a new one",
            tags = "user"
    )
    @PatchMapping("/user/profile/image")
    public ResponseEntity<String> updateUserProfileImage(@RequestBody @Valid UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException {
        return handle(userInfoService.updateProfileImage(userInfoImageUpdateRequest), HttpStatus.OK);
    }
}
