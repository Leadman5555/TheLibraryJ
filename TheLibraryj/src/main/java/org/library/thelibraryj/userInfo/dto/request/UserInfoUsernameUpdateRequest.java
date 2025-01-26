package org.library.thelibraryj.userInfo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.library.thelibraryj.infrastructure.validators.usernameCharacters.ValidUsernameCharacters;

public record UserInfoUsernameUpdateRequest(@Email String email, @NotNull @NotBlank @Size(min = 5, max = 20) @ValidUsernameCharacters String username) {
}
