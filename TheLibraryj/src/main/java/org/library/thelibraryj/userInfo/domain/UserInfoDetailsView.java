package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.EntityView;

import java.time.Instant;

@EntityView(UserInfo.class)
public interface UserInfoDetailsView {
    String getEmail();
    int getRank();
    int getCurrentScore();
    Instant getDataUpdatedAt();
    String getStatus();
    short getPreference();
}