package org.library.thelibraryj.book.dto;

import jakarta.validation.constraints.NotNull;

public record ChapterRequest(@NotNull Integer number, String title, String chapterText) { }
