package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.RatingRequest;
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
public class BookIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private static final String BASE_URL = TestProperties.BASE_URL + "/na/books";
    private static final String BASE_AUTH_URL = TestProperties.BASE_URL + "/books";
    private final UUID bookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID userId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID chapterId = UUID.fromString("123e4567-e89b-12d3-a456-999994174001");
    private static final String authorEmail1 = "sample.email1@gmail.com";
    private static final String authorEmail2 = "sample.email2@gmail.com";

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
    public void shouldReturnBookDetailById() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + '/' + bookId, String.class);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        JSONObject object = new JSONObject(response.getBody());
        Assertions.assertEquals("USER1", object.getString("author"));
    }

    @Test
    public void shouldAddAndUpdateRating() throws Exception {
        RatingRequest updateRatingRequest = new RatingRequest(
                authorEmail1,
                4,
                bookId,
                "sample"
        );
        HttpEntity<RatingRequest> requestEntity = new HttpEntity<>(updateRatingRequest, TestProperties.headers);
        ResponseEntity<String> ratingResponse = restTemplate.exchange(
                BASE_AUTH_URL + "/rating", HttpMethod.PUT, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), ratingResponse.getStatusCode().value());
        Assertions.assertNotNull(ratingResponse.getBody());
        RatingRequest createRatingRequest = new RatingRequest(
                authorEmail1,
                10,
                bookId,
                "sample"
        );
        HttpEntity<RatingRequest> requestEntity2 = new HttpEntity<>(createRatingRequest, TestProperties.headers);
        ResponseEntity<String> ratingResponse2 = restTemplate.exchange(
                BASE_AUTH_URL + "/rating", HttpMethod.PUT, requestEntity2, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), ratingResponse2.getStatusCode().value());
        Assertions.assertNotNull(ratingResponse2.getBody());
        RatingRequest createRatingRequest2 = new RatingRequest(
                authorEmail2,
                4,
                bookId,
                "sample"
        );
        TestProperties.fillHeadersForUser2();
        HttpEntity<RatingRequest> requestEntity3 = new HttpEntity<>(createRatingRequest2, TestProperties.headers);
        ResponseEntity<String> ratingResponse3 = restTemplate.exchange(
                BASE_AUTH_URL + "/rating", HttpMethod.PUT, requestEntity3, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), ratingResponse3.getStatusCode().value());
        Assertions.assertNotNull(ratingResponse3.getBody());


        ResponseEntity<String> previewResponse = restTemplate.getForEntity(
                BASE_URL + "/preview/" + bookId, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), previewResponse.getStatusCode().value());
        Assertions.assertNotNull(previewResponse.getBody());
        JSONObject object = new JSONObject(previewResponse.getBody());
        Assertions.assertEquals(2, object.getInt("ratingCount"));
        Assertions.assertEquals(7, object.getInt("averageRating"));
    }

    @Test
    public void shouldDeleteBookAndAllConnected() {
        ContentRemovalRequest request = new ContentRemovalRequest(bookId, authorEmail1);
        HttpEntity<ContentRemovalRequest> requestEntity = new HttpEntity<>(request, TestProperties.headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_AUTH_URL + "/book", HttpMethod.DELETE, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

        ResponseEntity<String> responseDetail = restTemplate.getForEntity(
                BASE_URL + '/' + bookId, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseDetail.getStatusCode().value());
        ResponseEntity<String> responsePreview = restTemplate.getForEntity(
                BASE_URL + "/preview/" + bookId, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responsePreview.getStatusCode().value());
        ContentRemovalRequest requestChapter = new ContentRemovalRequest(bookId, authorEmail1);
        HttpEntity<ContentRemovalRequest> requestEntityChapter = new HttpEntity<>(requestChapter, TestProperties.headers);
        ResponseEntity<String> responseChapter = restTemplate.exchange(
                BASE_AUTH_URL + "/chapter/" + 1, HttpMethod.DELETE, requestEntityChapter, String.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseChapter.getStatusCode().value());

    }
}
