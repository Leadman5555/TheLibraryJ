package org.library.thelibraryj.book.dto.bookDto;

import jakarta.validation.constraints.Size;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;
import org.library.thelibraryj.infrastructure.validators.titleCharacters.ValidTitleCharacters;
import org.springframework.lang.Nullable;

import java.util.List;

public record BookUpdateModel(@Nullable @Size(min = 5, max = 40) @ValidTitleCharacters String title,
                              @Nullable @Size(min = 50, max = 800) String description,
                              @Nullable BookState state,
                              @Nullable List<BookTag> bookTags,
                              boolean resetCoverImage) { }
