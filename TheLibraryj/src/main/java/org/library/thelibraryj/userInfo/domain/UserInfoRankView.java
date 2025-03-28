package org.library.thelibraryj.userInfo.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

import java.util.UUID;

@EntityView(UserInfo.class)
public interface UserInfoRankView {
    @IdMapping
    UUID getId();
    String getUsername();
    short getRank();
    int getCurrentScore();
    short getPreference();
}
