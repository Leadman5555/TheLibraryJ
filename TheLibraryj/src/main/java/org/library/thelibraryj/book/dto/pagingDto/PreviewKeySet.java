package org.library.thelibraryj.book.dto.pagingDto;

import com.blazebit.persistence.Keyset;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.UUID;

public record PreviewKeySet(int chapterCount, UUID bookId) implements Keyset {
    @JsonIgnore
    @Override
    public Serializable[] getTuple() {
        return new Serializable[]{chapterCount, bookId};
    }
}
