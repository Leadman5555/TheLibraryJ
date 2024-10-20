package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailResponse bookDetailToBookDetailResponse(BookDetail bookDetail, List<ChapterPreviewResponse> chapterPreviews,
                                                      List<RatingResponse> ratings);

    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview);

    List<ChapterPreviewResponse> chapterPreviewsToChapterPreviewResponseList(List<ChapterPreview> chapterPreviews);

    List<RatingResponse> ratingsToRatingResponseList(List<Rating> ratings);
}
