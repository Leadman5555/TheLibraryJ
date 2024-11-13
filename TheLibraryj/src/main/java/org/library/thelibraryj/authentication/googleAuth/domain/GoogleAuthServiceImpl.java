package org.library.thelibraryj.authentication.googleAuth.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.vavr.control.Either;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

@Service
class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserInfoService userInfoService;
    private final GoogleAuthProperties properties;
    private final WebClient googleWebClient;

    GoogleAuthServiceImpl(UserInfoService userInfoService, GoogleAuthProperties properties, WebClient googleWebClient) {
        this.userInfoService = userInfoService;
        this.properties = properties;
        this.googleWebClient = googleWebClient;
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
    public Either<GeneralError, String> getGoogleAuthToken(String code) throws IOException {
        //"https://oauth2.googleapis.com/token"
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                properties.getClientId(),
                properties.getClientSecret(),
                code,
                properties.getRedirectUri()
        ).execute();

        GoogleUserInfo onSuccessResponse = googleWebClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("access_token", tokenResponse.getIdToken()).build())
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
        createUserIfNotRegistered(onSuccessResponse); //Mono block() can never return null here
        return Either.right(tokenResponse.getIdToken());
    }

    private record GoogleUserInfo(String name, String family_name, String email){}

    private void createUserIfNotRegistered(GoogleUserInfo userData) {
        if (!userInfoService.existsByEmail(userData.email)) {
            String defaultUsername = userData.name + userData.family_name;
            if (defaultUsername.length() > 20) defaultUsername = defaultUsername.substring(0, 20);

            if (userInfoService.existsByUsername(defaultUsername)) {
                byte[] padding = new byte[24 - defaultUsername.length()];
                new Random().nextBytes(padding);
                defaultUsername += new String(padding, StandardCharsets.UTF_8);
            }

            userInfoService.createUserInfo(new UserInfoRequest(
                    defaultUsername,
                    userData.email,
                    properties.getDefault_google_id()
            ));
        }
    }

}
