package org.library.thelibraryj.book.dto;

import java.util.UUID;

public record BookPreviewResponse(String title,
                                  int chapterCount,
                                  float averageRating,
                                  int ratingCount,
                                  UUID bookDetailsId) {
}
