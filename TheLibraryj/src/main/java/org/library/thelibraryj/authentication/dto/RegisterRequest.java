package org.library.thelibraryj.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.library.thelibraryj.authentication.PasswordControl;

public record RegisterRequest(@Email String email, @NotNull @NotEmpty char[] password, @NotNull @Size(min = 5, max = 20) String username) implements PasswordControl {
}
