package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.time.Instant;
import java.util.UUID;

@EntityView(UserInfo.class)
public interface BookCreationUserView{
    @IdMapping
    @Mapping("id")
    UUID getAuthorId();
    @Mapping("username")
    String getAuthorUsername();
    Instant getCreatedAt();
}
