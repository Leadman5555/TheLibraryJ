package org.library.thelibraryj.book.dto.pagingDto;

import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.infrastructure.model.PageInfo;

import java.util.List;
import java.util.UUID;

public record PagedChapterPreviewResponse(List<ChapterPreviewResponse> content, PageInfo pageInfo, UUID bookId) {}
