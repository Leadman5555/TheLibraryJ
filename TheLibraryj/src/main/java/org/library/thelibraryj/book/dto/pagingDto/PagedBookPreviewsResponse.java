package org.library.thelibraryj.book.dto.pagingDto;

import com.blazebit.persistence.KeysetPage;
import org.library.thelibraryj.book.dto.bookDto.BookPreviewResponse;

import java.util.List;

public record PagedBookPreviewsResponse(List<BookPreviewResponse> content, int page, int totalPages, KeysetPage keysetPage) {}
