package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailsResponse bookDetailsToBookDetailsResponse(BookDetail bookDetail);
    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview);
}
