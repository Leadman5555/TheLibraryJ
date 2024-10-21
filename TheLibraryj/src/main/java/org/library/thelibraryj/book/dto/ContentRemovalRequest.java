package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ContentRemovalRequest(@NotNull UUID authorId, @NotNull UUID bookId) {
}
