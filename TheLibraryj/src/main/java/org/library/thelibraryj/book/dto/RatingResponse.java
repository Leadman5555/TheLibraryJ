package org.library.thelibraryj.book.dto;

import java.time.Instant;
import java.util.UUID;

public record RatingResponse(UUID userId, int currentRating, String comment, Instant updatedAt) {
}
