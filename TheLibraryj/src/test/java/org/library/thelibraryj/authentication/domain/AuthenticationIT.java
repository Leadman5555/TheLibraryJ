package org.library.thelibraryj.authentication.domain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration(classes = TheLibraryJApplication.class)
public class  AuthenticationIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private static final String BASE_URL = TestProperties.BASE_URL + "/na/auth";
    private static final UUID notEnabledUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource("schema.sql"));
        scriptExecutor.addScript(new ClassPathResource("dataInit.sql"));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
    }

    @Test
    public void shouldCreateUserAndSendEmail() throws Exception {
        final String email = "sample@email.com";
        final char[] password = "password".toCharArray();
        final String username = "sampleUsername";
        RegisterRequest requestEntity = new RegisterRequest(email, password, username, null);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                BASE_URL + "/register", requestEntity, String.class
        );

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
    public void shouldReturnTokenAfterSuccessfulAuthentication() throws Exception {
        final String existingEmail = "sample.email1@gmail.com";
        final String existingNonEnabledEmail = "sample.email2@gmail.com";
        final char[] validPassword = "password".toCharArray();
        AuthenticationRequest requestEntity = new AuthenticationRequest(existingEmail, validPassword);
        ResponseEntity<String> authResponse = restTemplate.postForEntity(
                BASE_URL, requestEntity, String.class
        );
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode().value());
        assertNotNull(authResponse.getBody());
        JSONObject object = new JSONObject(authResponse.getBody());
        assertTrue(object.has("token"));

        AuthenticationRequest invalidRequest = new AuthenticationRequest(existingEmail, ("invalidPassword").toCharArray());
        ResponseEntity<String> authResponse2 = restTemplate.postForEntity(
                BASE_URL, invalidRequest, String.class
        );
        assertEquals(HttpStatus.FORBIDDEN.value(), authResponse2.getStatusCode().value());

        AuthenticationRequest invalidRequest2 = new AuthenticationRequest("nonexistant@email.com", validPassword);
        ResponseEntity<String> authResponse3 = restTemplate.postForEntity(
                BASE_URL, invalidRequest2, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND.value(), authResponse3.getStatusCode().value());

        AuthenticationRequest notEnabledRequest = new AuthenticationRequest(existingNonEnabledEmail, validPassword);
        ResponseEntity<String> authResponse4 = restTemplate.postForEntity(
                BASE_URL, notEnabledRequest, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST.value(), authResponse4.getStatusCode().value());
    }

    @Test
    public void shouldResendActivationEmail() throws Exception {
        final String email = "sample.email2@gmail.com";
        final String username = "user2";

        Connection connection = dataSource.getConnection();

        Statement checkIfDisabled = connection.createStatement();
        checkIfDisabled.execute("SELECT is_enabled FROM library.library_user_auth WHERE id  = '" + notEnabledUserId + "'");
        ResultSet resultSetInfo = checkIfDisabled.getResultSet();
        resultSetInfo.next();
        assertFalse(resultSetInfo.getBoolean("is_enabled"));
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                BASE_URL + "/activation", String.class
        );
        assertEquals(HttpStatus.NO_CONTENT.value(), registerResponse.getStatusCode().value());

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(email).length == 1
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(email);
        assertEquals(1, receivedMessages.length);
        assertEquals(new AccountActivationTemplate("", "", Instant.now()).getSubject(), receivedMessages[0].getSubject());


        Statement checkCreatedAuth = connection.createStatement();
        checkCreatedAuth.execute("SELECT token FROM library.library_tokens WHERE for_user_id = '" + notEnabledUserId + "'");
        ResultSet resultSet = checkCreatedAuth.getResultSet();
        resultSet.next();
        UUID newToken = resultSet.getObject("token", UUID.class);

        ResponseEntity<String> activationResponse = restTemplate.exchange(
                BASE_URL + "/activation/" + newToken, HttpMethod.PATCH, null, String.class
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
