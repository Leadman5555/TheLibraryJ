package org.library.thelibraryj.authentication.authTokenServices.dto.password;

import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.infrastructure.validators.passwordCharacters.ValidPasswordCharacters;

import java.util.UUID;

public record PasswordResetRequest(@NotNull UUID tokenId, @NotNull @ValidPasswordCharacters char[] newPassword) {
}
