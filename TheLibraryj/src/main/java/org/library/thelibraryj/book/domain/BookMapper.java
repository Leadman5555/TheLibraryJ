package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
