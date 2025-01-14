package org.library.thelibraryj.book.dto.bookDto;

import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record BookUpdateRequest(@Nullable String title, @Nullable String description, @Nullable BookState state, @Nullable
                                MultipartFile coverImage, List<BookTag> bookTags, UUID bookId, String authorEmail) {
}
