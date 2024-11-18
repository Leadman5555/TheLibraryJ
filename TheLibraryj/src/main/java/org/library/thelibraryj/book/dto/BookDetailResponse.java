package org.library.thelibraryj.book.dto;

import java.util.List;

public record BookDetailResponse(String author, String description, List<ChapterPreviewResponse> chapterPreviews, List<RatingResponse> ratings) {
}
