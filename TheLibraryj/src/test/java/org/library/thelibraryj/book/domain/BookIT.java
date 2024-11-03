package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    private static final String BASE_URL =  "/v0.3" + "/books";
    private final UUID bookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID userId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID chapterId = UUID.fromString("123e4567-e89b-12d3-a456-999994174001");

    @BeforeEach
    public void setUp() {
        ResourceDatabasePopulator scriptExecutor = new ResourceDatabasePopulator();
        scriptExecutor.addScript(new ClassPathResource("schema.sql"));
        scriptExecutor.addScript(new ClassPathResource("dataInit.sql"));
        scriptExecutor.setSeparator("@@");
        scriptExecutor.execute(this.dataSource);
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
                userId,
                4,
                bookId,
                "sample"
        );
        HttpEntity<RatingRequest> requestEntity = new HttpEntity<>(updateRatingRequest);
        ResponseEntity<String> ratingResponse = restTemplate.exchange(
                BASE_URL + "/rating", HttpMethod.PUT, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), ratingResponse.getStatusCode().value());
        Assertions.assertNotNull(ratingResponse.getBody());
        RatingRequest createRatingRequest = new RatingRequest(
                userId2,
                10,
                bookId,
                "sample"
        );
        HttpEntity<RatingRequest> requestEntity2 = new HttpEntity<>(createRatingRequest);
        ResponseEntity<String> ratingResponse2 = restTemplate.exchange(
                BASE_URL + "/rating", HttpMethod.PUT, requestEntity2, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), ratingResponse2.getStatusCode().value());
        Assertions.assertNotNull(ratingResponse2.getBody());


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
    public void shouldDeleteBookAndAllConnected() throws Exception {
        ContentRemovalRequest request = new ContentRemovalRequest(userId, bookId);
        HttpEntity<ContentRemovalRequest> requestEntity = new HttpEntity<>(request);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/book", HttpMethod.DELETE, requestEntity, String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

        ResponseEntity<String> responseDetail = restTemplate.getForEntity(
                BASE_URL + '/' + bookId, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseDetail.getStatusCode().value());
        ResponseEntity<String> responsePreview = restTemplate.getForEntity(
                BASE_URL + "/preview/" + bookId, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responsePreview.getStatusCode().value());
        ContentRemovalRequest requestChapter = new ContentRemovalRequest(userId, bookId);
        HttpEntity<ContentRemovalRequest> requestEntityChapter = new HttpEntity<>(requestChapter);
        ResponseEntity<String> responseChapter = restTemplate.exchange(
                BASE_URL+"/book/chapter/" + 1, HttpMethod.DELETE, requestEntityChapter, String.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseChapter.getStatusCode().value());

    }
}
