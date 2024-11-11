package org.library.thelibraryj.jwtAuth.domain;

import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Spy
    private JwtProperties jwtProperties = new JwtProperties();

    @InjectMocks
    private JwtServiceImpl jwtService;

    private String token;
    private static final String subject = "sample.email1@gmail.com";

    @BeforeEach
    void setUp() {
        jwtProperties.setExpiration_time_ms(10000);
        jwtProperties.setSecret_key("d14ac166d8e4a37e663ea46dad662eb9f12ec4d2d3625ecf7be447917665eff8");
        token = jwtService.generateToken(subject);
    }

    @Test
    public void testExtractSubject() {
        Assertions.assertEquals(subject, jwtService.extractSubject(token));
    }

    @Test
    public void testValidateToken() {
        Assertions.assertTrue(jwtService.validateToken(token, subject));
        final String invalidToken = "invalid.token.here";
        Assertions.assertThrows(MalformedJwtException.class , () -> jwtService.validateToken(invalidToken, subject));
        Assertions.assertFalse(jwtService.validateToken(token, "otherSubject"));
    }
}
