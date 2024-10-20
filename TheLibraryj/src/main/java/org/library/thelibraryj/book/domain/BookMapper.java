package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.BookDetailResponse;
import org.library.thelibraryj.book.dto.BookPreviewResponse;
import org.library.thelibraryj.book.dto.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailResponse bookDetailToBookDetailResponse(BookDetail bookDetail, List<ChapterPreviewResponse> chapterPreviews,
                                                      List<RatingResponse> ratings);

    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview);

    @Mapping(source = "id", target = "chapterId")
    List<ChapterPreviewResponse> chapterPreviewsToChapterPreviewResponseList(List<ChapterPreview> chapterPreviews);

    List<RatingResponse> ratingsToRatingResponseList(List<Rating> ratings);

    static LocalDateTime map(Instant value){
        return LocalDateTime.ofInstant(value, ZoneOffset.ofHours(1));
    }
}
