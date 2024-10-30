package org.library.thelibraryj.authentication.userAuth.domain;

import org.springframework.security.core.GrantedAuthority;

enum UserRole implements GrantedAuthority {
    ADMIN(getCode.ADMIN),
    USER(getCode.USER);

    private final String authority;

    UserRole(final String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public static class getCode{
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}
