package org.library.thelibraryj.authentication.googleAuth.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vavr.control.Either;
import org.library.thelibraryj.authentication.googleAuth.GoogleAuthService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

@Service
class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserInfoService userInfoService;
    private final GoogleAuthProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    GoogleAuthServiceImpl(UserInfoService userInfoService, GoogleAuthProperties properties) {
        this.userInfoService = userInfoService;
        this.properties = properties;
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
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                properties.getClientId(),
                properties.getClientSecret(),
                code,
                properties.getRedirectUri()
        ).execute();
        final ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo?alt=json&access_token=" + tokenResponse.getAccessToken(),
                HttpMethod.GET,
                null,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) return Either.left(null);
        createUserIfNotRegistered(new Gson().fromJson(response.getBody(), JsonObject.class));
        return Either.right(tokenResponse.getIdToken());
    }

    private void createUserIfNotRegistered(JsonObject userDataJson) {
        final String email = userDataJson.get("email").getAsString();
        if (!userInfoService.existsByEmail(email)) {
            String defaultUsername = userDataJson.get("name").getAsString() + userDataJson.get("family_name").getAsString();
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
