package org.library.thelibraryj.book.dto;

import java.util.List;
import java.util.UUID;

public record BookDetailResponse(String author, UUID authorId, String description, List<ChapterPreviewResponse> chapterPreviews, List<RatingResponse> ratings) {
}
