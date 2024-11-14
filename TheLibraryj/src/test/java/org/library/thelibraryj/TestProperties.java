package org.library.thelibraryj;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


public class TestProperties {
    public static final String BASE_URL = "/v0.7";
    public static final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public static void fillHeaders() {
        headers.clear();
        final String alwaysValidTokenForUser1 = "eyJhbGciOiJFUzI1NiJ9.eyJpYXQiOjE3MzE2MTQ0OTEsInN1YiI6InNhbXBsZS5lbWFpbDFAZ21haWwuY29tIiwiaXNzIjoiYTM4MWU0Mjc5Zjg2NDk2MzljMjE3ZTk1Yjk4ZmMyYTA1NTU3Y2MxNmFjZGQ2Y2NmMWMzMDVkZjI2OGQzY2I4MyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MiIsImV4cCI6OTk5OTk5OTk5OX0.RGHc_rTxTzYIQpZL8iY85-iRWFsvSAyXg1oQt1RJH4ieinVTC6bvm812Pg6zvCFgjkDPEYgsVs_FjRdYb469pQ";
        headers.add("Authorization", ("Bearer " + alwaysValidTokenForUser1));
    }
}
