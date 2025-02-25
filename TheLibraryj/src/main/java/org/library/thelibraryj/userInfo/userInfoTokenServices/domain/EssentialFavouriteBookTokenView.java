package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;

import java.time.Instant;
import java.util.UUID;

@EntityView(FavouriteBookToken.class)
public interface EssentialFavouriteBookTokenView {
    Instant getExpiresAt();
    @Mapping("forUserId")
    UUID getUserId();
}
