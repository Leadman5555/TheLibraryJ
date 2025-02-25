package org.library.thelibraryj.book.dto.pagingDto;

import lombok.Getter;
import org.library.thelibraryj.book.dto.chapterDto.ChapterPreviewResponse;
import org.library.thelibraryj.infrastructure.model.paging.PageInfo;
import org.library.thelibraryj.infrastructure.model.paging.PagingResponseBase;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class PagedChapterPreviewResponse extends PagingResponseBase<ChapterPreviewResponse> {
    private final UUID bookId;

    public PagedChapterPreviewResponse(List<ChapterPreviewResponse> content, PageInfo pageInfo, UUID bookId) {
        super(content, pageInfo);
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PagedChapterPreviewResponse that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bookId);
    }
}
