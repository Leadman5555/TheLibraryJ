package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.util.UUID;

@EntityView(UserInfo.class)
public interface RatingUpsertView {
    @IdMapping
    @Mapping("id")
    UUID getUserId();
    String getUsername();
}
