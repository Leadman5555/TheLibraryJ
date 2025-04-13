package org.library.thelibraryj.authentication.domain;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestContextInitialization;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.authentication.dto.request.AuthenticationRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration(classes = TheLibraryJApplication.class)
public class AuthenticationIT extends TestContextInitialization {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private static final String BASE_URL = TestProperties.BASE_AUTH_FREE_URL + "/auth";
    private static final UUID notEnabledUserId = TestProperties.notEnabledUserId2;

    static final String existingEmail = TestProperties.userEmail1;
    static final String existingNonEnabledEmail = TestProperties.notEnabledUserEmail2;
    final char[] validPassword = TestProperties.allUserPassword.toCharArray();

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource("schema.sql"));
        scriptExecutor.addScript(new ClassPathResource("dataInit.sql"));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
    }

    @Test
    public void testCreateUserAndSendEmail() throws Exception {
        final String email = "sample@email.com";
        final char[] password = "P@ssword123".toCharArray();
        final String username = "sampleUsername";
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("email", email);
        multipartRequest.add("password", password);
        multipartRequest.add("username", username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, headers);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                BASE_URL + "/register",
                requestEntity,
                String.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(email).length == 1
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(email);
        assertEquals(1, receivedMessages.length);
        assertEquals(new AccountActivationTemplate("", "", Instant.now()).getSubject(), receivedMessages[0].getSubject());

        assertEquals(HttpStatus.CREATED.value(), registerResponse.getStatusCode().value());
        assertNotNull(registerResponse.getBody());
        JSONObject object = new JSONObject(registerResponse.getBody());
        assertEquals(username, object.getString("username"));
        assertEquals(email, object.getString("email"));
        assertFalse(object.getBoolean("isEnabled"));

        Connection connection = dataSource.getConnection();

        Statement checkCreatedAuth = connection.createStatement();
        checkCreatedAuth.execute("SELECT id FROM library.library_user_auth WHERE email = '" + email + "'");
        ResultSet resultSet = checkCreatedAuth.getResultSet();
        resultSet.next();
        UUID authUserId = resultSet.getObject("id", UUID.class);

        Statement checkCreatedInfo = connection.createStatement();
        checkCreatedInfo.execute("SELECT * FROM library.library_user_info WHERE user_auth_id = '" + authUserId + "'");
        ResultSet resultSetInfo = checkCreatedInfo.getResultSet();
        resultSetInfo.next();
        assertEquals(username, resultSetInfo.getString("username"));

        connection.close();
    }

    @Test
    public void testFailWithInvalidLogin() {
        AuthenticationRequest invalidRequest = new AuthenticationRequest(existingEmail, ("invalidPassword").toCharArray());
        ResponseEntity<String> authResponse2 = restTemplate.postForEntity(
                BASE_URL+"/login", invalidRequest, String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED.value(), authResponse2.getStatusCode().value());

        AuthenticationRequest invalidRequest2 = new AuthenticationRequest("nonexistant@email.com", validPassword);
        ResponseEntity<String> authResponse3 = restTemplate.postForEntity(
                BASE_URL+"/login", invalidRequest2, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND.value(), authResponse3.getStatusCode().value());

        AuthenticationRequest notEnabledRequest = new AuthenticationRequest(existingNonEnabledEmail, validPassword);
        ResponseEntity<String> authResponse4 = restTemplate.postForEntity(
                BASE_URL+"/login", notEnabledRequest, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST.value(), authResponse4.getStatusCode().value());
    }

    @Test
    public void testReturnTokenAndCookieAfterSuccessfulAuthentication() throws Exception {
        AuthenticationRequest requestEntity = new AuthenticationRequest(existingEmail, validPassword);
        ResponseEntity<String> authResponse = restTemplate.postForEntity(
                BASE_URL+"/login", requestEntity, String.class
        );
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode().value());
        assertNotNull(authResponse.getBody());
        JSONObject object = new JSONObject(authResponse.getBody());
        assertTrue(object.has("token"));

        List<String> cookies = authResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(cookies);
        Assertions.assertEquals(1, cookies.size());
        Assertions.assertTrue(cookies.getFirst().contains("refresh-token"));
    }

    @Test
    public void testRefreshTokenFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refresh-token=" + "InvalidToken");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/refresh",
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
        assertFalse(response.getHeaders().containsKey("access_token"));
    }

    @Test
    public void testRefreshTokenWithValidCookie() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refresh-token=" + TestProperties.alwaysValidTokenForUser1);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/refresh",
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        assertTrue(response.getHeaders().containsKey("access_token"));
    }



    @Test
    public void shouldResendActivationEmail() throws Exception {
        final String email = "sample.email2@gmail.com";

        Connection connection = dataSource.getConnection();
        Statement checkIfDisabled = connection.createStatement();
        checkIfDisabled.execute("SELECT is_enabled FROM library.library_user_auth WHERE id  = '" + notEnabledUserId + "'");
        ResultSet resultSetInfo = checkIfDisabled.getResultSet();
        resultSetInfo.next();
        assertFalse(resultSetInfo.getBoolean("is_enabled"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", email);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                BASE_URL + "/activation", request, String.class
        );
        assertEquals(HttpStatus.NO_CONTENT.value(), registerResponse.getStatusCode().value());

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(email).length == 1
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(email);
        assertEquals(1, receivedMessages.length);
        assertEquals(new AccountActivationTemplate("", "", Instant.now()).getSubject(), receivedMessages[0].getSubject());


        Statement checkCreatedAuth = connection.createStatement();
        checkCreatedAuth.execute("SELECT token FROM library.library_auth_tokens WHERE for_user_id = '" + notEnabledUserId + "'");
        ResultSet resultSet = checkCreatedAuth.getResultSet();
        resultSet.next();
        UUID newToken = resultSet.getObject("token", UUID.class);

        MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
        params2.add("token", newToken.toString());
        HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(params2, headers);

        ResponseEntity<String> activationResponse = restTemplate.exchange(
                BASE_URL + "/activation", HttpMethod.PATCH, request2, String.class
        );
        assertEquals(HttpStatus.NO_CONTENT.value(), activationResponse.getStatusCode().value());

        Statement checkIfEnabled = connection.createStatement();
        checkIfEnabled.execute("SELECT is_enabled FROM library.library_user_auth WHERE id  = '" + notEnabledUserId + "'");
        resultSetInfo = checkIfEnabled.getResultSet();
        resultSetInfo.next();
        assertTrue(resultSetInfo.getBoolean("is_enabled"));

        connection.close();
    }
}
