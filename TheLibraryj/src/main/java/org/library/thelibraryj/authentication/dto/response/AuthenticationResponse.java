package org.library.thelibraryj.authentication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;

public record AuthenticationResponse(@Schema(implementation = AuthToken.class) AuthToken token, Cookie refreshToken) {
}
