package org.library.thelibraryj;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;


public class TestProperties {
    public static final String BASE_URL = "/v0.9";
    public static final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    public static final String SCHEMA_FILE_NAME = "schema.sql";
    public static final String DATA_FILE_NAME = "dataInit.sql";

    public static final UUID bookId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final String bookTitle1 = "Book1";
    public static final UUID chapterId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    public static final UUID bookId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174006");
    public static final String bookTitle2 = "Book7D";
    public static final UUID noChapterBookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
    public static final UUID userId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID notEnabledUserId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    public static final String userEmail1 = "sample.email1@gmail.com";
    public static final String alwaysValidTokenForUser1 = "eyJhbGciOiJFUzI1NiJ9.eyJpYXQiOjE3MzE2MTQ0OTEsInN1YiI6InNhbXBsZS5lbWFpbDFAZ21haWwuY29tIiwiaXNzIjoiYTM4MWU0Mjc5Zjg2NDk2MzljMjE3ZTk1Yjk4ZmMyYTA1NTU3Y2MxNmFjZGQ2Y2NmMWMzMDVkZjI2OGQzY2I4MyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MiIsImV4cCI6OTk5OTk5OTk5OX0.RGHc_rTxTzYIQpZL8iY85-iRWFsvSAyXg1oQt1RJH4ieinVTC6bvm812Pg6zvCFgjkDPEYgsVs_FjRdYb469pQ";
    public static final String notEnabledUserEmail2 = "sample.email2@gmail.com";
    public static final String alwaysValidTokenForUser2 = "eyJhbGciOiJFUzI1NiJ9.eyJpYXQiOjE3MzE2MTQ0OTEsInN1YiI6InNhbXBsZS5lbWFpbDJAZ21haWwuY29tIiwiaXNzIjoiYTM4MWU0Mjc5Zjg2NDk2MzljMjE3ZTk1Yjk4ZmMyYTA1NTU3Y2MxNmFjZGQ2Y2NmMWMzMDVkZjI2OGQzY2I4MyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MiIsImV4cCI6OTk5OTk5OTk5OX0.0nUqjO5firsI4V62ZzFcGhr8qKQI7EqNCce5BWW2NBVAXRwFFXT4g5flc_CTiZDUbD28fgXgRQiL34eQaaX2ig";
    public static final String allUserPassword = "password";

    public static void fillHeadersForUser1() {
        headers.clear();
        headers.add("Authorization", ("Bearer " + alwaysValidTokenForUser1));
    }

    public static void fillHeadersForUser2() {
        headers.clear();
        headers.add("Authorization", ("Bearer " + alwaysValidTokenForUser2));
    }

    public static void addCSRFToken() {
        final String token = "csrf-token";
        headers.add("X-XSRF-TOKEN", token);
        headers.add(HttpHeaders.SET_COOKIE, "XSRF-TOKEN=" + token + "; Path=/");
    }
}
