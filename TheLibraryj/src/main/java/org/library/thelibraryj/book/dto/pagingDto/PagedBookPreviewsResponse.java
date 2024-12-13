package org.library.thelibraryj.book.dto.pagingDto;

import org.library.thelibraryj.book.dto.bookDto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.model.PageInfo;

import java.util.List;

public record PagedBookPreviewsResponse(List<BookPreviewResponse> content, PageInfo pageInfo) {}
