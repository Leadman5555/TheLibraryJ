package org.library.thelibraryj.book.dto;

import java.util.List;

public record BookDetailsResponse(String author, List<ChapterPreviewResponse> chapterPreviews, String description, List<RatingResponse> ratings) {
}
