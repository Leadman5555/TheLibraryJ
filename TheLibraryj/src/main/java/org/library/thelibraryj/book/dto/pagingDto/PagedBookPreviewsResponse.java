package org.library.thelibraryj.book.dto.pagingDto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.model.paging.PageInfo;
import org.library.thelibraryj.infrastructure.model.paging.PagingResponseBase;

import java.util.List;

public final class PagedBookPreviewsResponse extends PagingResponseBase<BookPreviewResponse> {
    public PagedBookPreviewsResponse(@ArraySchema(schema = @Schema(implementation = BookPreviewResponse.class)) List<BookPreviewResponse> content,
                                     @ArraySchema(schema = @Schema(implementation = PageInfo.class)) PageInfo pageInfo) {
        super(content, pageInfo);
    }
}
