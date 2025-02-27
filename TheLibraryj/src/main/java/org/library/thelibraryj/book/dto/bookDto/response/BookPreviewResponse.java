package org.library.thelibraryj.book.dto.bookDto.response;

import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;
import java.util.UUID;

public record BookPreviewResponse(String title,
                                  int chapterCount,
                                  float averageRating,
                                  int ratingCount,
                                  UUID id,
                                  BookState bookState,
                                  List<BookTag> bookTags,
                                  byte[] coverImage) {
}
