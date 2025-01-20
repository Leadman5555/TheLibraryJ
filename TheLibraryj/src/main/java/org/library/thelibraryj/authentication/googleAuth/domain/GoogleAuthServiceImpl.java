package org.library.thelibraryj.authentication.googleAuth.domain;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponse;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponseWrapper;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleLinkResponse;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.GoogleUserCreationRequest;
import org.library.thelibraryj.infrastructure.exception.GoogleApiNotRespondingException;
import org.library.thelibraryj.infrastructure.exception.GoogleTokenVerificationException;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Random;

@Service
class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserInfoService userInfoService;
    private final UserAuthService userAuthService;
    private final GoogleAuthProperties properties;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtService jwtService;

    GoogleAuthServiceImpl(UserInfoService userInfoService, GoogleAuthProperties properties, GoogleIdTokenVerifier googleIdTokenVerifier, JwtService jwtService, UserAuthService userAuthService) {
        this.userInfoService = userInfoService;
        this.properties = properties;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.jwtService = jwtService;
        this.userAuthService = userAuthService;
    }

    @Override
    public GoogleLinkResponse getGoogleAuthUrl() {
        return new GoogleLinkResponse(new GoogleAuthorizationCodeRequestUrl(
                properties.getClientId(),
                properties.getRedirectUri(),
                List.of("email", "profile")
        ).build());
    }

    @Override
    public GoogleCallbackResponseWrapper getGoogleAuthToken(String code) {
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
        StringBuilder sb = new StringBuilder((String) payload.get("given_name"));
        createUserIfNotRegistered(sb.append(((String) payload.get("family_name"))), payload.getEmail());
        return new GoogleCallbackResponseWrapper(
                new GoogleCallbackResponse(payload.getEmail(), jwtService.generateToken(payload.getEmail()).token()),
                jwtService.generateRefreshToken(payload.getEmail())
        );
    }

    private void createUserIfNotRegistered(StringBuilder defaultUsername, String email) {
        if (!userInfoService.existsByEmail(email)) {
            if (defaultUsername.length() > 20) defaultUsername.delete(21, defaultUsername.length());
            Random random = new Random();
            for (int i = 0; i < defaultUsername.length(); i++) {
                char c = defaultUsername.charAt(i);
                if ((c <= 'A' || c >= 'Z') && (c <= 'a' || c >= 'z') && (c <= '0' || c >= '9'))
                    defaultUsername.setCharAt(i, getRandomChar(random));
            }
            if (userInfoService.existsByUsername(defaultUsername.toString())) {
                char[] padding = new char[24 - defaultUsername.length()];
                for (int i = 0; i < padding.length; i++) padding[i] = getRandomChar(random);
                defaultUsername.append(padding);
            }
            userAuthService.createNewGoogleUser(new GoogleUserCreationRequest(
                    email,
                    defaultUsername.toString()
            ));
        }
    }

    private static char getRandomChar(Random random) {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return allowedCharacters.charAt(random.nextInt(allowedCharacters.length()));
    }

}
