package org.library.thelibraryj.book.dto.bookDto.request;

import org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile.ValidImageFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record BookUpdateRequest(@Nullable @ValidImageFormat MultipartFile coverImage, BookUpdateModel bookUpdateModel, UUID bookId, String authorEmail) {
}
