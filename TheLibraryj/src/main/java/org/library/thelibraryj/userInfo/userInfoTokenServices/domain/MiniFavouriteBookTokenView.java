package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import com.blazebit.persistence.view.EntityView;

import java.time.Instant;
import java.util.UUID;

@EntityView(FavouriteBookToken.class)
public interface MiniFavouriteBookTokenView {
    Instant getExpiresAt();
    int getUseCount();
    UUID getToken();
}
