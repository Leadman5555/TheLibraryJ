package org.library.thelibraryj.authentication.googleAuth.dto;

import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;

public record GoogleCallbackResponse(String email, AuthToken token) {
}
