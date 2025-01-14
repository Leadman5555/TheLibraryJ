package org.library.thelibraryj.authentication.jwtAuth.dto;

public record RefreshToken(String token, String domain, int maxAge) {
}
