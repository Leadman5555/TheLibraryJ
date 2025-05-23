package org.library.thelibraryj.authentication.authTokenServices.domain;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.EndpointsRegistry;
import org.library.thelibraryj.ITTestContextInitialization;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
public class PasswordResetIT extends ITTestContextInitialization {

    private static final String BASE_URL = EndpointsRegistry.PUBLIC_AUTH_PASSWORD_URL;
    private static final String user1email = TestProperties.userEmail1;
    private static final UUID user1Id = TestProperties.userId1;

    @BeforeEach
    public void setUp() {
        seedDB();
    }

    @Test
    public void shouldResetPassword() throws Exception {
        Connection connection = dataSource.getConnection();

        Statement getOldPassword = connection.createStatement();
        getOldPassword.execute("SELECT password FROM library.library_user_auth WHERE id = '" + user1Id + "'");
        ResultSet resultSetPassword = getOldPassword.getResultSet();
        resultSetPassword.next();
        String oldPassword = resultSetPassword.getString("password");


        final char[] newPasswordArray = "newPass4@".toCharArray();
        ResponseEntity<String> resetStartResponse = restTemplate.exchange(
                BASE_URL + '/' + user1email, HttpMethod.POST, null, String.class
        );
        assertEquals(HttpStatus.NO_CONTENT.value(), resetStartResponse.getStatusCode().value());

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(user1email).length == 1
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(user1email);
        assertEquals(1, receivedMessages.length);
        assertEquals(new PasswordResetTemplate("", Instant.now()).getSubject(), receivedMessages[0].getSubject());


        Statement checkCreatedToken = connection.createStatement();
        checkCreatedToken.execute("SELECT token FROM library.library_auth_tokens WHERE for_user_id = '" + user1Id + "'");
        ResultSet resultSet = checkCreatedToken.getResultSet();
        resultSet.next();
        UUID token = resultSet.getObject("token", UUID.class);

        PasswordResetRequest resetRequest = new PasswordResetRequest(token, newPasswordArray);
        ResponseEntity<String> resetResponse = restTemplate.exchange(
                BASE_URL, HttpMethod.PATCH, new HttpEntity<>(resetRequest), String.class
        );

        assertEquals(HttpStatus.NO_CONTENT.value(), resetResponse.getStatusCode().value());

        Statement checkTokenIsUsed = connection.createStatement();
        checkTokenIsUsed.execute("SELECT is_used FROM library.library_auth_tokens WHERE for_user_id = '" + user1Id + "'");
        ResultSet resultSetInfo = checkTokenIsUsed.getResultSet();
        resultSetInfo.next();
        assertTrue(resultSetInfo.getBoolean("is_used"));

        Statement checkPasswordReset = connection.createStatement();
        checkPasswordReset.execute("SELECT password FROM library.library_user_auth WHERE id = '" + user1Id + "'");
        ResultSet resultSetNewPassword = checkPasswordReset.getResultSet();
        resultSetNewPassword.next();
        String newPassword = resultSetNewPassword.getString("password");
        connection.close();

        assertNotEquals(oldPassword, newPassword);
    }
}