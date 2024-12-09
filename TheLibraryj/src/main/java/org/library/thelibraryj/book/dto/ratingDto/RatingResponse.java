package org.library.thelibraryj.book.dto.ratingDto;

import java.time.LocalDateTime;

public record RatingResponse(String username, int currentRating, String comment, LocalDateTime updatedAt) {
}
