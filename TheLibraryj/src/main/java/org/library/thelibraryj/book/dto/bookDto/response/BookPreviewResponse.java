package org.library.thelibraryj.book.dto.bookDto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.library.thelibraryj.book.domain.BookState;
import org.library.thelibraryj.book.domain.BookTag;

import java.util.List;
import java.util.UUID;

public record BookPreviewResponse(String title,
                                  int chapterCount,
                                  float averageRating,
                                  int ratingCount,
                                  UUID id,
                                  BookState bookState,
                                  @ArraySchema(schema = @Schema(implementation = BookTag.class)) List<BookTag> bookTags,
                                  String coverImageUrl) {
}
