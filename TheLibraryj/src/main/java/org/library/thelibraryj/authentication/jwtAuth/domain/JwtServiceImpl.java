package org.library.thelibraryj.authentication.jwtAuth.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
record JwtServiceImpl(JwtProperties properties) implements JwtService {
    @Override
    public String generateToken(String subject) {
        return generateToken(subject, new HashMap<>());
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claimsToInclude) {
        return Jwts.builder()
                .setClaims(claimsToInclude)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration_time_ms()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolveClaim) {
        return resolveClaim.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret_key()));
    }

    @Override
    public boolean validateToken(String token, String subject) {
        return extractSubject(token).equals(subject) && extractExpirationDate(token).after(new Date());
    }
}
