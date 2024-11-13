package org.library.thelibraryj.authentication.googleAuth.domain;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.vavr.control.Either;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.exception.GoogleApiNotRespondingException;
import org.library.thelibraryj.infrastructure.exception.GoogleTokenVerificationException;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Random;

@Service
class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserInfoService userInfoService;
    private final GoogleAuthProperties properties;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtService jwtService;

    GoogleAuthServiceImpl(UserInfoService userInfoService, GoogleAuthProperties properties, GoogleIdTokenVerifier googleIdTokenVerifier, JwtService jwtService) {
        this.userInfoService = userInfoService;
        this.properties = properties;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.jwtService = jwtService;
    }

    @Override
    public String getGoogleAuthUrl() {
        return new GoogleAuthorizationCodeRequestUrl(
                properties.getClientId(),
                properties.getRedirectUri(),
                List.of("email", "profile")
        ).build();
    }

    @Override
    public Either<GeneralError, String> getGoogleAuthToken(String code) {
        GoogleTokenResponse tokenResponse;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    properties.getClientId(),
                    properties.getClientSecret(),
                    code,
                    properties.getRedirectUri()
            ).execute();
        } catch (IOException e) {
            throw new GoogleApiNotRespondingException(e.getMessage());
        }
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(tokenResponse.getIdToken());
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleTokenVerificationException(e.getMessage());
        }
        if (idToken == null) throw new GoogleTokenVerificationException("Failed to verify idToken");
        GoogleIdToken.Payload payload = idToken.getPayload();
        createUserIfNotRegistered(payload.get("given_name") + ((String) payload.get("family_name")), payload.getEmail());
        return Either.right(jwtService.generateToken(payload.getEmail()));
    }

    private void createUserIfNotRegistered(String defaultUsername, String email) {
        if (!userInfoService.existsByEmail(email)) {
            if (defaultUsername.length() > 20) defaultUsername = defaultUsername.substring(0, 20);

            if (userInfoService.existsByUsername(defaultUsername)) {
                byte[] padding = new byte[24 - defaultUsername.length()];
                new Random().nextBytes(padding);
                defaultUsername += new String(padding, StandardCharsets.UTF_8);
            }

            userInfoService.createUserInfo(new UserInfoRequest(
                    defaultUsername,
                    email,
                    properties.getDefault_google_id()
            ));
        }
    }

}
