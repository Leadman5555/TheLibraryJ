package org.library.thelibraryj.book.dto.bookDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BookCreationRequest(@NotNull @Size(max = 40) String title, @Size(max = 700) String description, List<BookTag> tags, @Null MultipartFile coverImage, @NotNull String authorEmail) {
}
