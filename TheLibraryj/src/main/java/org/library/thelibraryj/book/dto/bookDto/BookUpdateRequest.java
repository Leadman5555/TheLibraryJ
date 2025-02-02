package org.library.thelibraryj.book.dto.bookDto;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record BookUpdateRequest(@Nullable MultipartFile coverImage, BookUpdateModel bookUpdateModel, UUID bookId, String authorEmail) {
}
