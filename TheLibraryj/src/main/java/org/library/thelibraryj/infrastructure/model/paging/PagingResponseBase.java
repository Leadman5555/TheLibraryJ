package org.library.thelibraryj.infrastructure.model.paging;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class PagingResponseBase<T> {
    private final List<T> content;
    private final PageInfo pageInfo;

    public PagingResponseBase(List<T> content, PageInfo pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PagingResponseBase<?> that)) return false;
        return Objects.equals(content, that.content) && Objects.equals(pageInfo, that.pageInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, pageInfo);
    }
}
