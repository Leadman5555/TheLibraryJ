package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.book.domain.BookState;

import java.util.UUID;

public record BookUpdateRequest(String title, String description, BookState state, @NotNull UUID bookId) {
}
