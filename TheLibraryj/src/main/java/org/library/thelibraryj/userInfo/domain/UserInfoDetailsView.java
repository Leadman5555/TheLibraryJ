package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.EntityView;

import java.time.Instant;

@EntityView(UserInfo.class)
public interface UserInfoDetailsView {
    short getRank();
    int getCurrentScore();
    Instant getDataUpdatedAt();
    Instant getCreatedAt();
    String getStatus();
    short getPreference();
}