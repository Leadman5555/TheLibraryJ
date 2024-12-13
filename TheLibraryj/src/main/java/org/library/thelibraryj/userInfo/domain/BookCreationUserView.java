package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.time.Instant;
import java.util.UUID;

public interface BookCreationUserView{
    @IdMapping
    @Mapping("id")
    UUID getAuthorId();
    @Mapping("username")
    String getAuthorUsername();
    Instant getCreatedAt();
}
