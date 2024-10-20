package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;
import java.util.UUID;

public record BookUpdateRequest(String title, String description, BookState state, List<BookTag> bookTags, @NotNull UUID bookId) {
}
