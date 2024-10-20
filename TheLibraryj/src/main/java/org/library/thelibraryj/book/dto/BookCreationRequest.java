package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;
import java.util.UUID;

public record BookCreationRequest(@NotNull String title, @NotNull UUID authorId, String description, List<BookTag> tags) {
}
