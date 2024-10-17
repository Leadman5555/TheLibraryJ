package org.library.thelibraryj.book.dto;

import java.util.List;

public record BookResponse(String title, String author, String description,
                           int chapterCount,
                           float averageRating,
                           int ratingCount,
                           List<ChapterPreviewResponse> chapters,
                           List<RatingResponse> ratings) {
}
