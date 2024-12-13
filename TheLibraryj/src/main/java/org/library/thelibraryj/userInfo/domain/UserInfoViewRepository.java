package org.library.thelibraryj.userInfo.domain;

interface UserInfoViewRepository {
    RatingUpsertView getRatingUpsertView(String email);
    BookCreationUserView getBookCreationUserView(String email);
}
