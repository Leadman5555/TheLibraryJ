package org.library.thelibraryj.authentication.googleAuth.dto;

import jakarta.servlet.http.Cookie;

public record GoogleCallbackResponseWrapper(GoogleCallbackResponse googleCallbackResponse, Cookie refreshToken) {
}
