package org.library.thelibraryj.book.dto;

import java.util.UUID;

public record RatingResponse(UUID userId, int currentRating) {
}
