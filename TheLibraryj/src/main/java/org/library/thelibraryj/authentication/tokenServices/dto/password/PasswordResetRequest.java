package org.library.thelibraryj.authentication.tokenServices.dto.password;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PasswordResetRequest(@NotNull UUID tokenId, @NotNull char[] newPassword) {
}
