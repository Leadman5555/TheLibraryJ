package org.library.thelibraryj.book.dto.sharedDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ContentRemovalRequest(@NotNull UUID bookId, @NotNull @Email String userEmail) {
}
