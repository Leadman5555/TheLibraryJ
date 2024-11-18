package org.library.thelibraryj.book.dto;

import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;

public record BookResponse(String title,
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
