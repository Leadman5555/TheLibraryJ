package org.library.thelibraryj.authentication.jwtAuth;

import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String subject);
    @Nullable
    UserDetails validateToken(String token);

}
