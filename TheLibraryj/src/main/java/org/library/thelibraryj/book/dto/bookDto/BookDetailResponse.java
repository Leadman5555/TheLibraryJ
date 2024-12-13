package org.library.thelibraryj.book.dto.bookDto;

import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;

import java.util.List;

public record BookDetailResponse(String author, String description, List<ChapterPreviewResponse> chapterPreviews, List<RatingResponse> ratings) {
}
