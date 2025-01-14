package org.library.thelibraryj.book.dto.bookDto;

import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BookCreationRequest(String title, String description, List<BookTag> tags, MultipartFile coverImage, String authorEmail) {
}
