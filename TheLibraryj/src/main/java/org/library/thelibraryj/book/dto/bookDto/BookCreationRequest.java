package org.library.thelibraryj.book.dto.bookDto;

import org.springframework.web.multipart.MultipartFile;

public record BookCreationRequest(BookCreationModel bookCreationModel, MultipartFile coverImage, String authorEmail) {
}
