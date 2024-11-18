package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RatingRequest(@NotNull String userEmail, @NotNull @Min(1) @Max(10) int currentRating, @NotNull UUID bookId,
                            @Size(max = 200) String comment) {
}
