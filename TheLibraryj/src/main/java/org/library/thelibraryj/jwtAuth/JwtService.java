package org.library.thelibraryj.jwtAuth;

import java.util.Map;

public interface JwtService {
    String generateToken(String subject);
    String generateToken(String subject, Map<String, Object> claimsToInclude);
    String extractSubject(String token);
    boolean validateToken(String token, String subject);

}
