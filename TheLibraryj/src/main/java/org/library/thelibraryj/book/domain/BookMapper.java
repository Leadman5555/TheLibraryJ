package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.BookResponse;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface BookMapper {
    //@Mapping(source = "chapterPreviews", target = "chapterPreviews", qualifiedByName = "chapterP")
    BookDetailResponse bookDetailToBookDetailResponse(BookDetail bookDetail);
    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview);
    BookResponse bookToBookResponse(BookDetail bookDetail, BookPreview bookPreview);
    List<ChapterPreviewResponse> chapterPreviewsToChapterPreviewResponseList(List<ChapterPreview> chapterPreviews);
    List<RatingResponse> ratingsToRatingResponseList(List<Rating> ratings);
}
