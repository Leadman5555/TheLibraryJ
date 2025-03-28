package org.library.thelibraryj.book.dto.pagingDto;

import com.blazebit.persistence.Keyset;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public record PreviewKeySet(int number, UUID id) implements Keyset {
    @JsonCreator
    public PreviewKeySet(@JsonProperty("tuple") Serializable[] tuple) {
        this((int) tuple[0], UUID.fromString((String) tuple[1]));
    }

    @JsonIgnore
    @Override
    public Serializable[] getTuple() {
        return new Serializable[]{number, id};
    }
}
