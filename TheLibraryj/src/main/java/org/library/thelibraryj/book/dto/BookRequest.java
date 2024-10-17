package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;

public record BookRequest(@NotNull String title, @NotNull String author, String description) {
}
