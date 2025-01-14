package org.library.thelibraryj.authentication.jwtAuth;

import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    AuthToken generateToken(String subject);
    Cookie generateRefreshToken(String subject);
    Cookie clearRefreshToken();
    @Nullable
    UserDetails validateToken(String token);
}
