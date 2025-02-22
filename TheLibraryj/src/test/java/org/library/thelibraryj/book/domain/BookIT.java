package org.library.thelibraryj.book.domain;

import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.TheLibraryJApplication;
import org.library.thelibraryj.book.dto.ratingDto.RatingRequest;
import org.library.thelibraryj.book.dto.sharedDto.ContentRemovalRequest;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TheLibraryJApplication.class)
@ContextConfiguration
public class BookIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @TempDir
    private Path temporaryDir;

    private static final String BASE_URL = TestProperties.BASE_URL + "/na/books";
    private static final String BASE_AUTH_URL = TestProperties.BASE_URL + "/books";
    private final UUID bookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID chapterBookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
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
    public void testGetByParams() throws Exception {
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
        Assertions.assertEquals(2, body2.length());

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
        Assertions.assertEquals(3, body3.length());
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
        Assertions.assertEquals(2, body4.length());
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

    private record JsonPagedKeysetPage(int firstResult, int maxResults, TupleKeyset highest, TupleKeyset lowest,
                                       List<TupleKeyset> keysets) {
    }

    private record TupleKeyset(Serializable[] tuple) {
    }

    @Test
    public void testPagingByKeySet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("page", 0);
        params.add("pageSize", 2);
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
        Assertions.assertEquals(2, previews.length());
        Assertions.assertEquals(validBookTitle7, previews.getJSONObject(0).getString("title"));
        Assertions.assertEquals(2, body.getJSONObject("pageInfo").getInt("totalPages"));
        JSONObject nextKSJSON = body.getJSONObject("pageInfo").getJSONObject("keysetPage");

        JSONArray tupleLowest = nextKSJSON.getJSONObject("lowest").getJSONArray("tuple");
        JSONArray tupleHighest = nextKSJSON.getJSONObject("highest").getJSONArray("tuple");
        TupleKeyset lowest = new TupleKeyset(new Serializable[]{tupleLowest.getInt(0), UUID.fromString(tupleLowest.getString(1))});
        TupleKeyset highest = new TupleKeyset(new Serializable[]{tupleHighest.getInt(0), UUID.fromString(tupleHighest.getString(1))});
        JsonPagedKeysetPage nextKS = new JsonPagedKeysetPage(nextKSJSON.getInt("firstResult"), nextKSJSON.getInt("maxResults"), lowest, highest, List.of());

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(BASE_URL)
                .queryParam("page", 1);
        HttpEntity<JsonPagedKeysetPage> request2 = new HttpEntity<>(nextKS, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                request2,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response2.getStatusCode().value());
        Assertions.assertNotNull(response2.getBody());
        JSONObject body2 = new JSONObject(response2.getBody());
        JSONArray previews2 = body2.getJSONArray("content");
        Assertions.assertEquals(2, previews2.length());
        Assertions.assertEquals(validBookTitle1, previews2.getJSONObject(0).getString("title"));
        Assertions.assertEquals(1, body2.getJSONObject("pageInfo").getInt("page"));
        JSONObject nextKSJSON2 = body.getJSONObject("pageInfo").getJSONObject("keysetPage");

        tupleLowest = nextKSJSON2.getJSONObject("lowest").getJSONArray("tuple");
        tupleHighest = nextKSJSON2.getJSONObject("highest").getJSONArray("tuple");
        lowest = new TupleKeyset(new Serializable[]{tupleLowest.getInt(0), UUID.fromString(tupleLowest.getString(1))});
        highest = new TupleKeyset(new Serializable[]{tupleHighest.getInt(0), UUID.fromString(tupleHighest.getString(1))});
        nextKS = new JsonPagedKeysetPage(nextKSJSON.getInt("firstResult"), nextKSJSON.getInt("maxResults"), lowest, highest, List.of());

        builder = UriComponentsBuilder.fromPath(BASE_URL)
                .queryParam("page", 2);
        HttpEntity<JsonPagedKeysetPage> request3 = new HttpEntity<>(nextKS, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                request3,
                String.class
        );
        Assertions.assertEquals(HttpStatus.OK.value(), response3.getStatusCode().value());
        Assertions.assertNotNull(response3.getBody());
        JSONArray content3 = new JSONObject(response3.getBody()).getJSONArray("content");
        Assertions.assertEquals(0, content3.length());
    }

    private final class MockFile {

        private final Path path;
        @Getter
        private final String content;

        MockFile(String filename, int length, String mediaType) throws IOException {
            this.path = temporaryDir.resolve(filename);
            File tempFile = Files.createFile(path).toFile();
            String data = getMockData(length);
            this.content = data;
            switch (mediaType) {
                case MediaType.TEXT_PLAIN_VALUE -> createTxtFile(data, this.path);
                case wordType2 -> createDocxFile(data, tempFile);
                case libreOfficeType -> createOdfFile(data, tempFile);
                default -> throw new IllegalArgumentException("Unsupported media type: " + mediaType);
            }
        }

        private static void createOdfFile(String data, File saveToFile){
            try (OdfTextDocument document = OdfTextDocument.newTextDocument()) {
                document.addText(data);
                document.save(saveToFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static void createTxtFile(String data, Path saveToFile){
            try {
                Files.writeString(saveToFile, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static void createDocxFile(String data, File saveToFile){
            try (XWPFDocument document = new XWPFDocument()) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.createRun().setText(data);
                document.write(new FileOutputStream(saveToFile));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static String getMockData(int length) {
            Random random = new Random();
            StringBuilder result = new StringBuilder(length);
            for (int i = 0; i < length; i++) result.append(characters.charAt(random.nextInt(characters.length())));
            return result.toString();
        }

    }

    private static final String libreOfficeType = "application/vnd.oasis.opendocument.text";
    private static final String wordType = "application/msword";
    private static final String wordType2 = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final String chapterFetchQuery = "SELECT * FROM library.library_chapters LEFT OUTER JOIN library.library_chapter_previews lcp ON library_chapters.chapter_preview_id = lcp.id WHERE lcp.book_detail_id = '" + chapterBookId + "' ORDER BY lcp.number";
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";



    private ResponseEntity<String> upsertChaptersRequest(List<MockFile> fileList) {
        HttpHeaders headers = new HttpHeaders(TestProperties.headers);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("authorEmail", authorEmail1);
        for (MockFile file : fileList) parts.add("chapterBatch", new PathResource(file.path));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
        return restTemplate.exchange(
                BASE_AUTH_URL + "/book/" + chapterBookId + "/chapter", HttpMethod.PUT, requestEntity, String.class
        );
    }
    
    @Test
    public void testUpsertChapters_createAndUpdateChapters() throws Exception {
        List<MockFile> fileList = List.of(
                new MockFile("100 - Valid1.txt", 100, MediaType.TEXT_PLAIN_VALUE),
                new MockFile("200 - Valid2.docx", 1000, wordType2),
                new MockFile("300.odt", 500, libreOfficeType)
        );
        var response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        JSONArray body = new JSONArray(response.getBody());
        assert body.length() == 3;
        Assertions.assertEquals(100, body.getJSONObject(0).getInt("number"));
        Assertions.assertEquals(200, body.getJSONObject(1).getInt("number"));
        Assertions.assertEquals(300, body.getJSONObject(2).getInt("number"));
        Assertions.assertEquals("Valid1", body.getJSONObject(0).getString("title"));
        Assertions.assertEquals("Valid2", body.getJSONObject(1).getString("title"));
        Assertions.assertEquals("No title", body.getJSONObject(2).getString("title"));

        Connection connection = dataSource.getConnection();
        Statement checkCreatedChapters = connection.createStatement();
        checkCreatedChapters.execute(chapterFetchQuery);
        ResultSet resultSet = checkCreatedChapters.getResultSet();
        resultSet.next();
        Assertions.assertEquals(100, resultSet.getInt("number"));
        Assertions.assertEquals("Valid1", resultSet.getString("title"));
        Assertions.assertEquals(fileList.getFirst().getContent(), resultSet.getString("text"));
        resultSet.next();
        Assertions.assertEquals(200, resultSet.getInt("number"));
        Assertions.assertEquals("Valid2", resultSet.getString("title"));
        Assertions.assertEquals(fileList.get(1).getContent(), resultSet.getString("text"));
        resultSet.next();
        Assertions.assertEquals(300, resultSet.getInt("number"));
        Assertions.assertEquals("No title", resultSet.getString("title"));
        Assertions.assertEquals(fileList.get(2).getContent(), resultSet.getString("text"));
        connection.close();

        List<MockFile> updateFileList = List.of(
                new MockFile("100 - newValid1.docx", 100, wordType2),
                new MockFile("400.txt", 1000, MediaType.TEXT_PLAIN_VALUE),
                new MockFile("300 - chapter3.txt", 500, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(updateFileList);
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());

        connection = dataSource.getConnection();
        checkCreatedChapters = connection.createStatement();
        checkCreatedChapters.execute(chapterFetchQuery);
        resultSet = checkCreatedChapters.getResultSet();
        resultSet.next();
        Assertions.assertEquals(100, resultSet.getInt("number"));
        Assertions.assertEquals("newValid1", resultSet.getString("title"));
        Assertions.assertEquals(updateFileList.getFirst().getContent(), resultSet.getString("text"));
        resultSet.next();
        resultSet.next();
        Assertions.assertEquals(300, resultSet.getInt("number"));
        Assertions.assertEquals("chapter3", resultSet.getString("title"));
        Assertions.assertEquals(updateFileList.get(2).getContent(), resultSet.getString("text"));
        resultSet.next();
        Assertions.assertEquals(400, resultSet.getInt("number"));
        Assertions.assertEquals("No title", resultSet.getString("title"));
        Assertions.assertEquals(updateFileList.get(1).getContent(), resultSet.getString("text"));
        connection.close();
    }

    @Test
    public void testUpsertChapters_invalidFiles() throws Exception {
        List<MockFile> fileList = List.of(
                new MockFile("100 - invalid_#$%$#%@#$#@!$.txt", 100, MediaType.TEXT_PLAIN_VALUE)
        );
        var response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("100 : invalid.txt", 100, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("C:\\Users\\John\\Documents\\1.2 - invalid.txt", 100, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("invalid.doc", 100, wordType)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("100 -  .txt", 100, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("100 - valid.exe", 100, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("100 -  valid.png", 100, libreOfficeType)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        fileList = List.of(
                new MockFile("100 - valid.txt", 20000, MediaType.TEXT_PLAIN_VALUE)
        );
        response = upsertChaptersRequest(fileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());

        List<MockFile> tooLongFileList = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            tooLongFileList.add(new MockFile(i + " - valid.txt", 1, MediaType.TEXT_PLAIN_VALUE));
        response = upsertChaptersRequest(tooLongFileList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());


        List<MockFile> duplicateFileNumberList = new ArrayList<>();
        for (int i = 0; i < 3; i++) tooLongFileList.add(new MockFile("1 - valid.txt", 1, MediaType.TEXT_PLAIN_VALUE));
        response = upsertChaptersRequest(duplicateFileNumberList);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());

    }
}
