package org.library.thelibraryj.book.dto.bookDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BookCreationRequest(@NotNull String title, String description, List<BookTag> tags, @Null MultipartFile coverImage, @NotNull String authorEmail) {
}
