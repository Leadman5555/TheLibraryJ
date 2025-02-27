package org.library.thelibraryj.authentication.dto.response;

import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;

public record AuthenticationResponse(AuthToken token, Cookie refreshToken) {
}
