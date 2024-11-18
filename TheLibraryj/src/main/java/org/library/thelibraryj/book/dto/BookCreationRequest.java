package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record BookCreationRequest(@NotNull String title, @NotNull UUID authorId, String description, List<BookTag> tags, @Null MultipartFile coverImage) {
}
