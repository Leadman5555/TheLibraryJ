package org.library.thelibraryj.userInfo.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.library.thelibraryj.infrastructure.error.ErrorHandling;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${library.mapping}/user")
record UserInfoController(UserInfoService userInfoService) implements ErrorHandling {


    @Operation(
            summary = "Fetch a single UserInfo record by Id",
            tags = "user"
    )
    @GetMapping("/{id}")
    public ResponseEntity<String> getUserInfoResponseById(@PathVariable("id") UUID id) {
        return handle(userInfoService.getUserInfoResponseById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Update user's rank (negative change means decreasing the rank)",
            tags = "user"
    )
    @PatchMapping("/profile/rank")
    public ResponseEntity<String> updateUserInfoRank(@RequestBody @Valid UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        return handle(userInfoService.updateRank(userInfoRankUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Change the user's username and reflect the change on all his books",
            tags = "user"
    )
    @PatchMapping("/profile/username")
    public ResponseEntity<String> updateUserInfoUsername(@RequestBody @Valid UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        return handle(userInfoService.updateUserInfoUsername(userInfoUsernameUpdateRequest), HttpStatus.OK);
    }
}
