package org.library.thelibraryj.authentication.userAuth.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.util.UUID;

@EntityView(UserAuth.class)
public interface PasswordResetView {
    @IdMapping
    @Mapping("id")
    UUID getUserAuthId();

    @Mapping("isGoogle")
    boolean getIsGoogleUser();
}
