package org.library.thelibraryj.book.dto;

import java.util.List;
import java.util.UUID;

public record BookDetailResponse(String author, UUID authorId, List<ChapterPreviewResponse> chapterPreviews, String description, List<RatingResponse> ratings) {
}
