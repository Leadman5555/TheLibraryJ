package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.userAuth.domain.UserRole;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Spy
    private final JwtProperties jwtProperties = new JwtProperties();

    static final String publicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEHMberk3xVepnUlc1p17905sSBmYJ+9IS6UKHgsfm8kYGe4QsYASumkY6vG0WtQc77Mqot9jXQaYqVxHYinNYXg==";
    static final String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCJo7IN6eWnORQPrc8+TdICImuwtZK/+KhY+Bf9EYaMKA==";
    @Spy
    private final Algorithm jwtSigningAlgorithm = Algorithm.ECDSA256(
            (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey))),
            (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey))));

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private String token;
    private static final String subject = "sample.email1@gmail.com";

    JwtServiceTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
    }

    @BeforeEach
    void setUp() {
        jwtProperties.setExpiration_time_ms(10000000);
        jwtProperties.setClient_id("d14ac166d8e4a37e663ea46dad662eb9f12ec4d2d3625ecf7be447917665eff8");
        jwtProperties.setAud("http://localhost:8082");
        token = jwtService.generateToken(subject).token();
    }

    @Test
    public void testValidateToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
        when(userDetailsService.loadUserByUsername(subject)).thenReturn(getMockDetails(subject));
        Assertions.assertNotNull(jwtService.validateToken(token));
        final String invalidToken = "invalid.token.here";
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(invalidToken));
        final String invalidIssuerToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer("invalidIssuer")
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(jwtSigningAlgorithm);
        Assertions.assertNull(jwtService.validateToken(invalidIssuerToken));
        final String invalidAudienceToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience("invalidAudience")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(jwtSigningAlgorithm);
        Assertions.assertNull(jwtService.validateToken(invalidAudienceToken));
        final String expiredToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() - jwtProperties.getExpiration_time_ms()))
                .sign(jwtSigningAlgorithm);
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(expiredToken));
        final String wrongSigningAlgorithmKeysToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(Algorithm.ECDSA512(
                        (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decodeBase64("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEZMVTuZekl2DqUJv02l+CoG8vWkNUaBQRTsQLmBYnXyMlKz0RP+7GES419Jb4o37EE3nkzSZ0NFBkJC4AOmeCRw=="))),
                        (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCmlFCeDJxkd5HI4BEDJsO1YBPLVuOHRNbgwn+nwv0IuA==")))));
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(wrongSigningAlgorithmKeysToken));
        final String wrongPKAlgorithmToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(Algorithm.ECDSA512(
                        (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey))),
                        (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCmlFCeDJxkd5HI4BEDJsO1YBPLVuOHRNbgwn+nwv0IuA==")))));
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(wrongPKAlgorithmToken));
        final String wrongSigningAlgorithmToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(subject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(Algorithm.HMAC256("a381e4279f8649639c217e95b98fc2a05557cc16acdd6ccf1c305df268d3cb83"));
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(wrongSigningAlgorithmToken));
        final String nonExistingSubject = "otherSubject";
        when(userDetailsService.loadUserByUsername(nonExistingSubject)).thenThrow(new UsernameNotFoundException(nonExistingSubject));
        final String nonExistingSubjectToken = JWT.create()
                .withIssuedAt(new Date())
                .withSubject(nonExistingSubject)
                .withIssuer(jwtProperties.getClient_id())
                .withAudience(jwtProperties.getAud())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration_time_ms()))
                .sign(jwtSigningAlgorithm);
        Assertions.assertThrows(Exception.class, () -> jwtService.validateToken(nonExistingSubjectToken));

    }

    @SuppressWarnings("SameParameterValue")
    private static UserDetails getMockDetails(String forSubject) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(UserRole.ROLE_USER);
            }

            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return forSubject;
            }
        };
    }
}
