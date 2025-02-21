package org.library.thelibraryj.userInfo.domain;

import java.util.Optional;

interface UserInfoViewRepository {
    Optional<RatingUpsertView> getRatingUpsertView(String email);

    Optional<BookCreationUserView> getBookCreationUserView(String email);

    Optional<UserInfoDetailsView> getUserInfoDetailsView(String username);

    Optional<UserInfoMiniView> getUserInfoMiniView(String email);
}
