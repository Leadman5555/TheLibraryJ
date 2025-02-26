package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserInfoTokenServicesViewRepository {

    Optional<MiniFavouriteBookTokenView> fetchByUserIdAndToken(UUID userId, UUID token);
    Optional<MiniFavouriteBookTokenView> fetchByUserId(UUID userId);
    Optional<EssentialFavouriteBookTokenView> fetchByToken(UUID token);
}
