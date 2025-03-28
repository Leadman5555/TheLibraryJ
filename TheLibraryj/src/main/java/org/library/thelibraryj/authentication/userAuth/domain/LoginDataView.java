package org.library.thelibraryj.authentication.userAuth.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@EntityView(UserAuth.class)
public interface LoginDataView {
    default Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        return Collections.singleton(getUserRole());
    }

    @Mapping("role")
    UserRole getUserRole();

    boolean getIsEnabled();

    @Mapping("isGoogle")
    boolean getIsGoogleUser();
}
