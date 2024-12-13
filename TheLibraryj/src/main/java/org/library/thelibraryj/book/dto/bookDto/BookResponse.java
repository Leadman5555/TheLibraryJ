package org.library.thelibraryj.book.dto.bookDto;

import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;

import java.util.List;
import java.util.UUID;

public record BookResponse(UUID id,
                           String title,
                           String author,
                           String description,
                           int chapterCount,
                           float averageRating,
                           int ratingCount,
                           List<ChapterPreviewResponse> chapterPreviews,
                           List<RatingResponse> ratings,
                           List<BookTag> bookTags,
                           BookState bookState,
                           byte[] coverImage) {
}
