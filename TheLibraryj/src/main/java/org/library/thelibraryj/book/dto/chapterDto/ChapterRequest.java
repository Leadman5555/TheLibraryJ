package org.library.thelibraryj.book.dto.chapterDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChapterRequest(@NotNull @Max(10000) Integer number, @Size(max = 40) String title, @Size(max = 18000) String chapterText, @NotNull UUID bookId, @NotNull String authorEmail) { }
