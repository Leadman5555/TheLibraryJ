package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChapterRequest(@NotNull @Max(10000) Integer number, String title, @Size(max = 18000) String chapterText, @NotNull UUID bookId, @NotNull UUID authorId) { }
