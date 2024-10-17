package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailsResponse bookDetailsToBookDetailsResponse(BookDetail bookDetail);
}
