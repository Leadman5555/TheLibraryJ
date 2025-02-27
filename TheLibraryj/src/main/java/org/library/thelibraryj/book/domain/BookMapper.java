package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.book.dto.bookDto.response.BookDetailResponse;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterPreviewResponse;
import org.library.thelibraryj.book.dto.chapterDto.response.ChapterResponse;
import org.library.thelibraryj.book.dto.ratingDto.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
interface BookMapper {
    BookDetailResponse bookDetailToBookDetailResponse(BookDetail bookDetail);

    BookPreviewResponse bookPreviewToBookPreviewResponse(BookPreview bookPreview, byte[] coverImage);
    BookPreviewResponse bookPreviewWithCoverToBookPreviewResponse(BookPreview bookPreview, byte[] coverImage);

    @Mapping(source = "id", target = "chapterId")
    List<ChapterPreviewResponse> chapterPreviewsToChapterPreviewResponseList(List<ChapterPreview> chapterPreviews);

    List<RatingResponse> ratingsToRatingResponseList(List<Rating> ratings);

    @Mapping(source = "id", target = "chapterId")
    ChapterPreviewResponse chapterPreviewToChapterPreviewResponse(ChapterPreview chapterPreview);

    @Mapping(source = "text", target = "content")
    ChapterResponse chapterDataToChapterResponse(String text, String title);

    static LocalDateTime map(Instant value){
        return LocalDateTime.ofInstant(value, ZoneOffset.ofHours(1));
    }
}
