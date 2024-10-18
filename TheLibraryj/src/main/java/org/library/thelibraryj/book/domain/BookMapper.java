package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailResponse bookDetailToBookDetailResponse(BookDetail bookDetail);
    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview);
    BookResponse bookToBookResponse(BookDetail bookDetail, BookPreview bookPreview);
}
