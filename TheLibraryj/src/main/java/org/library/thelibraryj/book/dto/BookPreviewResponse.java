package org.library.thelibraryj.book.dto;

import org.library.thelibraryj.book.domain.BookState;

import java.util.UUID;

public record BookPreviewResponse(String title,
                                  int chapterCount,
                                  float averageRating,
                                  int ratingCount,
                                  UUID id,
                                  BookState bookState) {
}
