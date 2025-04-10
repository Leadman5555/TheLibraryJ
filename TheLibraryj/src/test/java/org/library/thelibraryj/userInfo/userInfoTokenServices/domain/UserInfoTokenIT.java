package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.library.thelibraryj.TestContextInitialization;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration
@Import(TestProperties.class)
public class UserInfoTokenIT extends TestContextInitialization {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);

    private static final String BASE_URL = TestProperties.BASE_URL + "/user/book/token";

    private static final UUID bookId = TestProperties.bookId1;
    private static final UUID bookId2 = TestProperties.bookId2;
    private static final UUID bookId3 = UUID.randomUUID();
    private static final String email = TestProperties.userEmail1;
    private static final UUID userId = TestProperties.userId1;
    private static final UUID userId2 = TestProperties.googleUserId3;
    private static final String email2 = TestProperties.googleUserEmail3;

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource(TestProperties.SCHEMA_FILE_NAME));
        scriptExecutor.addScript(new ClassPathResource(TestProperties.DATA_FILE_NAME));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
        TestProperties.fillHeadersForUser1();
    }

    private static final String checkBookTokensQuery = "SELECT * FROM library.library_book_tokens WHERE for_user_id= '" + userId + "'";

    @Test
    public void testTokenPipeline() throws Exception {
        BookTokenRequest body = new BookTokenRequest(email);
        HttpEntity<BookTokenRequest> requestEntity = new HttpEntity<>(body, TestProperties.headers);
        ResponseEntity<String> bookTokenResponse1 = restTemplate.exchange(
                BASE_URL, HttpMethod.PUT, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, bookTokenResponse1.getStatusCode());
        Assertions.assertNotNull(bookTokenResponse1.getBody());
        JSONObject body1 = new JSONObject(bookTokenResponse1.getBody());
        String receivedToken = body1.getString("token");
        Assertions.assertNotNull(receivedToken);
        Assertions.assertTrue(body1.getBoolean("justCreated"));
        Assertions.assertEquals(0, body1.getInt("useCount"));

        Connection connection = dataSource.getConnection();
        Statement checkBookTokens = connection.createStatement();
        checkBookTokens.execute(checkBookTokensQuery);
        ResultSet resultSet = checkBookTokens.getResultSet();
        resultSet.next();
        Assertions.assertEquals(receivedToken, resultSet.getString("token"));
        Assertions.assertFalse(resultSet.next());
        connection.close();

        ResponseEntity<String> bookTokenResponse2 = restTemplate.exchange(
                BASE_URL, HttpMethod.PUT, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, bookTokenResponse2.getStatusCode());
        Assertions.assertNotNull(bookTokenResponse2.getBody());
        JSONObject body2 = new JSONObject(bookTokenResponse2.getBody());
        String receivedToken2 = body2.getString("token");
        Assertions.assertNotNull(receivedToken);
        Assertions.assertFalse(body2.getBoolean("justCreated"));
        Assertions.assertEquals(0, body2.getInt("useCount"));

        Assertions.assertEquals(receivedToken, receivedToken2);

        connection = dataSource.getConnection();
        checkBookTokens = connection.createStatement();
        checkBookTokens.execute(checkBookTokensQuery);
        resultSet = checkBookTokens.getResultSet();
        resultSet.next();
        Assertions.assertEquals(receivedToken, resultSet.getString("token"));
        Assertions.assertFalse(resultSet.next());
        connection.close();

        BookTokenConsummationRequest emailRequest = new BookTokenConsummationRequest(UUID.fromString(receivedToken), email);
        HttpEntity<BookTokenConsummationRequest> emailRequestEntity = new HttpEntity<>(emailRequest, TestProperties.headers);
        ResponseEntity<String> emailResponse = restTemplate.exchange(
                BASE_URL + "/email", HttpMethod.POST, emailRequestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.NO_CONTENT, emailResponse.getStatusCode());
        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(email).length == 1
        );

        BookTokenConsummationRequest emailRequest2 = new BookTokenConsummationRequest(UUID.randomUUID(), email);
        emailRequestEntity = new HttpEntity<>(emailRequest2, TestProperties.headers);
        ResponseEntity<String> emailResponse2 = restTemplate.exchange(
                BASE_URL + "/email", HttpMethod.POST, emailRequestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, emailResponse2.getStatusCode());
    }

    private static final String clearFavouriteBooksQuery = "DELETE FROM library.favourite_books WHERE user_info_id= '" + userId + "' OR user_info_id= '" + userId2 + "'";
    private static final String addFavouriteBookQuery = "INSERT INTO library.favourite_books (user_info_id, book_id) VALUES ('" + userId + "', '" + bookId + "'), ('" + userId + "', '" + bookId2 + "'), ('" + userId2 + "', '" + bookId + "') , ('" + userId2 + "', '" + bookId3 + "')";
    private static final UUID validTokenId = UUID.randomUUID();
    private static final String addValidBookTokenForUser1Query = "INSERT INTO library.library_book_tokens (id, token, for_user_id, version, created_at, updated_at, expires_at, use_count) VALUES ('123e4567-e89b-12d3-a456-426614174001', '" + validTokenId + "', '" + userId + "', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,        CURRENT_TIMESTAMP + 10, 0)";
    private static final String checkFavouriteBooksQueryForUser2 = "SELECT book_id FROM library.favourite_books WHERE user_info_id= '" + userId2 + "' ORDER BY book_id";
    private static final String checkFavouriteBooksQueryForUser1 = "SELECT book_id FROM library.favourite_books WHERE user_info_id= '" + userId + "' ORDER BY book_id";


    @Test
    public void testFavouriteBookMerging() throws Exception {
        Connection connection = dataSource.getConnection();
        Statement fillDb = connection.createStatement();
        fillDb.execute(clearFavouriteBooksQuery);
        fillDb.execute(addFavouriteBookQuery);
        fillDb.execute(addValidBookTokenForUser1Query);
        connection.close();

        BookTokenConsummationRequest consummationRequest = new BookTokenConsummationRequest(validTokenId, email2);
        HttpEntity<BookTokenConsummationRequest> emailRequestEntity = new HttpEntity<>(consummationRequest, TestProperties.headers);
        ResponseEntity<String> mergerResponse = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, emailRequestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, mergerResponse.getStatusCode());
        JSONObject body = new JSONObject(mergerResponse.getBody());
        Assertions.assertEquals(2, body.getInt("sizeBeforeMerge"));
        Assertions.assertEquals(3, body.getInt("sizeAfterMerge"));
        Assertions.assertEquals(2, body.getInt("attemptedToMergeCount"));


        connection = dataSource.getConnection();
        Statement checkFavouriteBooks = connection.createStatement();
        checkFavouriteBooks.execute(checkFavouriteBooksQueryForUser2);
        ResultSet resultSet = checkFavouriteBooks.getResultSet();
        resultSet.next();
        Assertions.assertEquals(bookId, resultSet.getObject("book_id", UUID.class));
        resultSet.next();
        Assertions.assertEquals(bookId2, resultSet.getObject("book_id", UUID.class));
        resultSet.next();
        Assertions.assertEquals(bookId3, resultSet.getObject("book_id", UUID.class));
        Assertions.assertFalse(resultSet.next());
        checkFavouriteBooks.execute(checkFavouriteBooksQueryForUser1);
        resultSet = checkFavouriteBooks.getResultSet();
        resultSet.next();
        Assertions.assertEquals(bookId, resultSet.getObject("book_id", UUID.class));
        resultSet.next();
        Assertions.assertEquals(bookId2, resultSet.getObject("book_id", UUID.class));
        Assertions.assertFalse(resultSet.next());
        connection.close();
    }
}
