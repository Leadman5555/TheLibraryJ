package org.library.thelibraryj.book.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.book.dto.ContentRemovalRequest;
import org.library.thelibraryj.book.dto.PreviewKeySet;
import org.library.thelibraryj.book.dto.PreviewKeySetPage;
import org.library.thelibraryj.book.dto.RatingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
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
import java.util.List;
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
    private static final String validBookTitle7 = "Book7D";
    private static final String validBookTitle1 = "Book1";

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

    @Test
    public void testGetByParams() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("titleLike", "Book");
        params.add("minChapters", 100);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/filtered",
                HttpMethod.GET,
                request,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        JSONArray body = new JSONArray(response.getBody());
        Assertions.assertEquals(1, body.length());

        MultiValueMap<String, Object> params2 = new LinkedMultiValueMap<>();
        params2.add("minRating", 4.67);
        HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(params2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(
                BASE_URL + "/filtered",
                HttpMethod.GET,
                request2,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response2.getStatusCode().value());
        Assertions.assertNotNull(response2.getBody());
        JSONArray body2 = new JSONArray(response2.getBody());
        Assertions.assertEquals(1, body2.length());

        HttpEntity<MultiValueMap<String, Object>> request3 = new HttpEntity<>(null, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(
                BASE_URL + "/filtered",
                HttpMethod.GET,
                request3,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response3.getStatusCode().value());
        Assertions.assertNotNull(response3.getBody());
        JSONArray body3 = new JSONArray(response3.getBody());
        Assertions.assertEquals(2, body3.length());
        HttpEntity<MultiValueMap<String, String>> request4 = new HttpEntity<>(null, headers);
        ResponseEntity<String> response4 = restTemplate.exchange(
                BASE_URL + "/filtered?state=HIATUS",
                HttpMethod.GET,
                request4,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response4.getStatusCode().value());
        Assertions.assertNotNull(response4.getBody());
        JSONArray body4 = new JSONArray(response4.getBody());
        Assertions.assertEquals(1, body4.length());
        HttpEntity<MultiValueMap<String, Object>> request5 = new HttpEntity<>(null, headers);
        ResponseEntity<String> response5 = restTemplate.exchange(
                BASE_URL + "/filtered?hasTags=TAG1&hasTags=TAG2",
                HttpMethod.GET,
                request5,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response5.getStatusCode().value());
        Assertions.assertNotNull(response5.getBody());
        JSONArray body5 = new JSONArray(response5.getBody());
        Assertions.assertEquals(1, body5.length());
    }

    @Test
    public void testPagingByKeySet() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("page", 0);
        params.add("pageSize", 1);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        JSONObject body = new JSONObject(response.getBody());
        JSONArray previews = body.getJSONArray("content");
        Assertions.assertEquals(1, previews.length());
        Assertions.assertEquals(validBookTitle7, previews.getJSONObject(0).getString("title"));
        Assertions.assertEquals(2, body.getInt("totalPages"));
        JSONObject nextKSJSON = body.getJSONObject("keysetPage");

        JSONArray tupleLowest = nextKSJSON.getJSONObject("lowest").getJSONArray("tuple");
        JSONArray tupleHighest = nextKSJSON.getJSONObject("highest").getJSONArray("tuple");
        PreviewKeySet lowest = new PreviewKeySet(tupleLowest.getInt(0), tupleLowest.getString(1));
        PreviewKeySet highest = new PreviewKeySet(tupleHighest.getInt(0), tupleHighest.getString(1));
        PreviewKeySetPage nextKS = new PreviewKeySetPage(nextKSJSON.getInt("firstResult"), nextKSJSON.getInt("maxResults"), lowest, highest, List.of());

        MultiValueMap<String, Object> params2 = new LinkedMultiValueMap<>();
        params2.add("page", 1);
        HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(params2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request2,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response2.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        JSONObject body2 = new JSONObject(response.getBody());
        JSONArray previews2 = body2.getJSONArray("content");
        Assertions.assertEquals(1, previews2.length());
        Assertions.assertEquals(validBookTitle1, previews2.getJSONObject(0).getString("title"));
        Assertions.assertEquals(2, body2.getInt("page"));
        Object nextKS2 = body2.get("keysetPage");

        MultiValueMap<String, Object> params3 = new LinkedMultiValueMap<>();
        params2.add("page", 2);
        params2.add("keyset", nextKS2);
        HttpEntity<MultiValueMap<String, Object>> request3 = new HttpEntity<>(params3, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request3,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response3.getStatusCode().value());
        Assertions.assertNotNull(response3.getBody());
        JSONArray body3 = new JSONArray(response3.getBody());
        Assertions.assertEquals(0, body3.length());
    }
}
