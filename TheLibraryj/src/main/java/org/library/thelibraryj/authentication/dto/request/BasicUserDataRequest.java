package org.library.thelibraryj.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record BasicUserDataRequest(@NotNull String username, @Email @NotNull String email) {
}
