package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookCreationRequest(@NotNull String title, @NotNull UUID authorId, String description) {
}
