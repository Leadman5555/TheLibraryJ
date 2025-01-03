package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
record JwtServiceImpl(JwtProperties properties, Algorithm jwtSigningAlgorithm,
                      UserDetailsService userDetailsService) implements JwtService {

    private String generateTokenString(String subject, int expirationTimeMs) {
        return JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(properties.getClient_id())
                .withAudience(properties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMs))
                .sign(jwtSigningAlgorithm);
    }

    @Override
    public AuthToken generateToken(String subject) {
        return new AuthToken(generateTokenString(subject, properties.getExpiration_time_ms()));
    }

    @Override
    public Cookie generateRefreshToken(String subject) {
        final Cookie refreshToken = new Cookie("refresh-token", generateTokenString(subject, properties.getExpiration_time_s_refresh()*1000));
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");
        refreshToken.setDomain(properties.getRefresh_domain());
        refreshToken.setMaxAge(properties.getExpiration_time_s_refresh());
        refreshToken.setSecure(properties.isSend_secure());
        return refreshToken;
    }

    @Override
    public Cookie clearRefreshToken() {
        final Cookie clearRefreshToken = new Cookie("refresh-token", "loggedOut");
        clearRefreshToken.setHttpOnly(true);
        clearRefreshToken.setPath("/");
        clearRefreshToken.setDomain(properties.getRefresh_domain());
        clearRefreshToken.setMaxAge(0);
        clearRefreshToken.setSecure(properties.isSend_secure());
        return clearRefreshToken;
    }

    @Override
    @Nullable
    public UserDetails validateToken(String token) {
        DecodedJWT decodedJWT = JWT.require(jwtSigningAlgorithm).build().verify(token);
        if (!decodedJWT.getAudience().getFirst().equals(properties.getAud())
                || !decodedJWT.getIssuer().equals(properties.getClient_id())) return null;
        return userDetailsService.loadUserByUsername(decodedJWT.getSubject());
    }
}
