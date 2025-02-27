package org.library.thelibraryj.book.dto.pagingDto;

import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.model.paging.PageInfo;
import org.library.thelibraryj.infrastructure.model.paging.PagingResponseBase;

import java.util.List;

public final class PagedBookPreviewsResponse extends PagingResponseBase<BookPreviewResponse> {
    public PagedBookPreviewsResponse(List<BookPreviewResponse> content, PageInfo pageInfo) {
        super(content, pageInfo);
    }
}
