package org.library.thelibraryj.book.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RatingResponse(UUID userId, int currentRating, String comment, LocalDateTime updatedAt) {
}
