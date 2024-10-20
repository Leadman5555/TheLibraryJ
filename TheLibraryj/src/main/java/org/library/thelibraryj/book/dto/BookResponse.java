package org.library.thelibraryj.book.dto;

import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;
import java.util.UUID;

public record BookResponse(String title,
                           String author,
                           UUID authorId,
                           String description,
                           int chapterCount,
                           float averageRating,
                           int ratingCount,
                           List<ChapterPreviewResponse> chapterPreviews,
                           List<RatingResponse> ratings,
                           List<BookTag> bookTags,
                           BookState bookState) {
}
