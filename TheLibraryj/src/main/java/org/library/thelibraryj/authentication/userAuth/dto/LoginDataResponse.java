package org.library.thelibraryj.authentication.userAuth.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record LoginDataResponse(Collection<? extends GrantedAuthority> grantedAuthorities, boolean isEnabled, boolean isGoogleUser) { }
