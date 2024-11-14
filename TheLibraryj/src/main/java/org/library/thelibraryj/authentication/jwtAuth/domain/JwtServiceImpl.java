package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
record JwtServiceImpl(JwtProperties properties, Algorithm jwtSigningAlgorithm, UserDetailsService userDetailsService) implements JwtService {
    @Override
    public String generateToken(String subject) {
        return JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(properties.getClient_id())
                .withAudience(properties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + properties.getExpiration_time_ms()))
                .sign(jwtSigningAlgorithm);
    }

    @Override
    @Nullable
    public UserDetails validateToken(String token) {
        DecodedJWT decodedJWT =  JWT.require(jwtSigningAlgorithm).build().verify(token);
        UserDetails fetchedDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        if(!fetchedDetails.getUsername().equals(decodedJWT.getSubject())) return null;
        if(decodedJWT.getExpiresAt().before(new Date())) return null;
        return fetchedDetails;
    }
}
