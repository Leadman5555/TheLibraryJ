package org.library.thelibraryj;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


public class TestProperties {
    public static final String BASE_URL = "/v0.6";
    public static final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public static void fillHeaders() {
        headers.clear();
        final String alwaysValidTokenForUser1 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW1wbGUuZW1haWwxQGdtYWlsLmNvbSIsImlhdCI6MTczMTMyMjE5NCwiZXhwIjo5OTk5OTk5OTk5fQ.WT26Q3mS5Rb9VzIf8PoiJ1Z1IYBmdLi_poYBCPTQyZM";
        headers.add("Authorization", ("Bearer " + alwaysValidTokenForUser1));
    }
}
