package org.library.thelibraryj.infrastructure.imageHandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.ITTestContextInitialization;
import org.library.thelibraryj.TestProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageHandlingIT extends ITTestContextInitialization {

    private static final String URL_BASE = TestProperties.BASE_AUTH_FREE_URL + "/image/";

    @Test
    public void shouldReturnDefaultImage() {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(URL_BASE + getHandlerName(0) + "/default", byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(getDefaultImage(), response.getBody());
    }

    @Test
    public void shouldReturnSavedImage() throws Exception {
        int handlerIndex = 0;
        String fileId = "shouldReturnSavedImage";
        byte[] file = storeFile(fileId + ".jpg", handlerIndex, (byte) 23);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(URL_BASE + getHandlerName(handlerIndex) + '/' + fileId, byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(file, response.getBody());
    }

    @Test
    public void shouldReturnNotFoundWhenNoImageIsFound() {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(URL_BASE + getHandlerName(0) + "/notFound_imageName?fail_on_not_found=true", byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidHandlerGiven() {
        String invalidHandlerName = getHandlerName(0) + "_invalid";
        ResponseEntity<byte[]> response = restTemplate.getForEntity(URL_BASE + invalidHandlerName + "/notFound?fail_on_not_found=true", byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        response = restTemplate.getForEntity(URL_BASE + invalidHandlerName + "/notFound", byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldUseCorrectHandler() throws Exception {
        int handler0 = 0;
        String fileId0 = "handler0_file";
        byte[] file0 = storeFile(fileId0 + ".jpg", handler0, (byte) 23);
        String fileId1 = "handler1_file";
        int handler1 = 1;
        byte[] file1 = storeFile(fileId1 + ".jpg", handler1, (byte) 253);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(URL_BASE + getHandlerName(handler0) + '/' + fileId0, byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(file0, response.getBody());
        response = restTemplate.getForEntity(URL_BASE + getHandlerName(handler1) + '/' + fileId1, byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(file1, response.getBody());

        response = restTemplate.getForEntity(URL_BASE + getHandlerName(handler0) + '/' + fileId1 + "?fail_on_not_found=true", byte[].class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
