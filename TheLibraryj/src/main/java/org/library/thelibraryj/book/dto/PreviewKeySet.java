package org.library.thelibraryj.book.dto;

import com.blazebit.persistence.Keyset;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public record PreviewKeySet(int chapterCount, String bookId) implements Keyset {
    @JsonIgnore
    @Override
    public Serializable[] getTuple() {
        return new Serializable[]{chapterCount, bookId};
    }
}
