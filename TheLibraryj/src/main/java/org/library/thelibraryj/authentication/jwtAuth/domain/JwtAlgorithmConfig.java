package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Configuration
@RequiredArgsConstructor
class JwtAlgorithmConfig {

    @Value("${library.auth.jwt.public_key}")
    private String publicKey;
    @Value("${library.auth.jwt.private_key}")
    private String privateKey;

    @Bean
    public Algorithm jwtSigningAlgorithm() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return Algorithm.ECDSA512(
                (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey))),
                (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey)))
        );
    }

}
