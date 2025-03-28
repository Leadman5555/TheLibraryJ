package org.library.thelibraryj.userInfo.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration
public class UserInfoIT {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private static final String BASE_URL = TestProperties.BASE_URL + "/user";
    private static final String BASE_AUTH_FREE_URL = TestProperties.BASE_AUTH_FREE_URL;
    private static final UUID bookId = TestProperties.bookId1;
    private static final UUID bookId2 = TestProperties.bookId2;
    private static final UUID userId = TestProperties.userId1;
    private static final String email = TestProperties.userEmail1;

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource(TestProperties.SCHEMA_FILE_NAME));
        scriptExecutor.addScript(new ClassPathResource(TestProperties.DATA_FILE_NAME));
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
                 BASE_AUTH_FREE_URL + "/books/" + bookId, String.class);
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

    private static final String checkFavouriteBooksQuery = "SELECT * FROM library.favourite_books WHERE user_info_id = '" + userId + "' ORDER BY book_id";

    @Test
    public void testFavouriteBookPipeline() throws Exception {
        Connection connection = dataSource.getConnection();
        Statement checkFavouriteBooks = connection.createStatement();
        checkFavouriteBooks.execute(checkFavouriteBooksQuery);
        ResultSet resultSet = checkFavouriteBooks.getResultSet();
        Assertions.assertFalse(resultSet.next());
        connection.close();

        final String url = BASE_URL + "/book?email=" + email;
        HttpEntity<String> requestEntity = new HttpEntity<>(null, TestProperties.headers);

        ResponseEntity<String> fetchFavouriteBooks = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, fetchFavouriteBooks.getStatusCode());
        Assertions.assertNotNull(fetchFavouriteBooks.getBody());
        JSONArray fetchedPreviews = new JSONArray(fetchFavouriteBooks.getBody());
        Assertions.assertEquals(0, fetchedPreviews.length());

        ResponseEntity<String> addBookToFavouritesRequest1 = restTemplate.exchange(
                url+"&bookId="+bookId, HttpMethod.POST, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, addBookToFavouritesRequest1.getStatusCode());
        Assertions.assertNotNull(addBookToFavouritesRequest1.getBody());
        Assertions.assertEquals(1, Integer.parseInt(addBookToFavouritesRequest1.getBody()));
        ResponseEntity<String> addBookToFavouritesRequest2 = restTemplate.exchange(
                url+"&bookId="+bookId2, HttpMethod.POST, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, addBookToFavouritesRequest2.getStatusCode());
        Assertions.assertNotNull(addBookToFavouritesRequest2.getBody());
        Assertions.assertEquals(2, Integer.parseInt(addBookToFavouritesRequest2.getBody()));

        ResponseEntity<String> fetchFavouriteBooks2 = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK, fetchFavouriteBooks2.getStatusCode());
        Assertions.assertNotNull(fetchFavouriteBooks2.getBody());
        fetchedPreviews = new JSONArray(fetchFavouriteBooks2.getBody());
        Assertions.assertEquals(2, fetchedPreviews.length());
        List<String> sortedIdList = new ArrayList<>();
        sortedIdList.add(fetchedPreviews.getJSONObject(0).getString("id")); sortedIdList.add(fetchedPreviews.getJSONObject(1).getString("id"));
        sortedIdList.sort(String::compareTo);
        Assertions.assertEquals(bookId.toString(), sortedIdList.get(0));
        Assertions.assertEquals(bookId2.toString(), sortedIdList.get(1));

        connection = dataSource.getConnection();
        checkFavouriteBooks = connection.createStatement();
        checkFavouriteBooks.execute(checkFavouriteBooksQuery);
        resultSet = checkFavouriteBooks.getResultSet();
        resultSet.next();
        Assertions.assertEquals(userId.toString(), resultSet.getString("user_info_id"));
        Assertions.assertEquals(bookId.toString(), resultSet.getString("book_id"));
        resultSet.next();
        Assertions.assertEquals(userId.toString(), resultSet.getString("user_info_id"));
        Assertions.assertEquals(bookId2.toString(), resultSet.getString("book_id"));
        Assertions.assertFalse(resultSet.next());
        connection.close();

        ResponseEntity<String> removeBooksFromFavouriteRequest = restTemplate.exchange(
               url+"&bookId="+bookId2, HttpMethod.DELETE, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.NO_CONTENT, removeBooksFromFavouriteRequest.getStatusCode());
        Thread.sleep(100); //delete is async, wait for it to finish
        connection = dataSource.getConnection();
        checkFavouriteBooks = connection.createStatement();
        checkFavouriteBooks.execute(checkFavouriteBooksQuery);
        resultSet = checkFavouriteBooks.getResultSet();
        resultSet.next();
        Assertions.assertEquals(userId.toString(), resultSet.getString("user_info_id"));
        Assertions.assertEquals(bookId.toString(), resultSet.getString("book_id"));
        Assertions.assertFalse(resultSet.next());
        connection.close();
    }
}