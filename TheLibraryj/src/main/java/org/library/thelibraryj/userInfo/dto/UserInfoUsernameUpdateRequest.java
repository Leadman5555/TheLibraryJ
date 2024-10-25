package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserInfoUsernameUpdateRequest(@NotNull UUID userId, @NotNull @NotBlank @Size(min = 5, max = 20) String username) {
}
