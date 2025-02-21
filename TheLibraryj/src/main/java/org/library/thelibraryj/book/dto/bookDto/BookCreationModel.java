package org.library.thelibraryj.book.dto.bookDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.infrastructure.validators.titleCharacters.ValidTitleCharacters;

import java.util.List;

public record BookCreationModel(@NotNull @Size(min = 5, max = 40) @ValidTitleCharacters String title,
                                @Size(min = 50, max = 800) String description,
                                @NotEmpty List<BookTag> tags) { }
