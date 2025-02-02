package org.library.thelibraryj.userInfo.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration
public class UserInfoIT {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private static final String VERSION = TestProperties.BASE_URL;
    private static final String BASE_URL = VERSION + "/user";
    private final UUID bookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String email = "sample.email1@gmail.com";

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource("schema.sql"));
        scriptExecutor.addScript(new ClassPathResource("dataInit.sql"));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
        TestProperties.fillHeadersForUser1();
    }

    @Test
    public void shouldUpdateUserUsernameAndBookDetailAuthor() throws Exception {
        final String newUsername = "newUsername";
        UserInfoUsernameUpdateRequest request = new UserInfoUsernameUpdateRequest(
                email,
                newUsername
        );
        HttpEntity<UserInfoUsernameUpdateRequest> requestEntity = new HttpEntity<>(request, TestProperties.headers);
        ResponseEntity<String> usernameChangeResponse = restTemplate.exchange(
                BASE_URL + "/profile/username", HttpMethod.PATCH, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, usernameChangeResponse.getStatusCode());
        Assertions.assertNotNull(usernameChangeResponse.getBody());
        JSONObject object = new JSONObject(usernameChangeResponse.getBody());
        Assertions.assertEquals(newUsername, object.getString("newUsername"));

        ResponseEntity<String> userBookDetailResponse = restTemplate.getForEntity(
                VERSION + "/na/books/" + bookId, String.class);
        Assertions.assertEquals(HttpStatus.OK.value(), userBookDetailResponse.getStatusCode().value());
        Assertions.assertNotNull(userBookDetailResponse.getBody());
        JSONObject object2 = new JSONObject(userBookDetailResponse.getBody());
        Assertions.assertEquals(newUsername, object2.getString("author"));

        ResponseEntity<String> usernameAlreadyExistingResponse = restTemplate.exchange(
                BASE_URL + "/profile/username", HttpMethod.PATCH, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), usernameAlreadyExistingResponse.getStatusCode().value());

        UserInfoUsernameUpdateRequest request2 = new UserInfoUsernameUpdateRequest(
                email,
                "otherNew_username"
        );
        HttpEntity<UserInfoUsernameUpdateRequest> requestEntity2 = new HttpEntity<>(request2, TestProperties.headers);
        ResponseEntity<String> usernameOnCooldownChangeResponse = restTemplate.exchange(
                BASE_URL + "/profile/username", HttpMethod.PATCH, requestEntity2, String.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), usernameOnCooldownChangeResponse.getStatusCode().value());
    }
}