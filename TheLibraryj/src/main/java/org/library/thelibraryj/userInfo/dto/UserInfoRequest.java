package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserInfoRequest(@NotBlank String username, @NotBlank String email, @NotNull UUID userAuthId) {
}
