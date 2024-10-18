package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.springframework.http.ResponseEntity;

public interface ErrorHandling {
    ObjectMapper objectMapper = new ObjectMapper();
    default ResponseEntity<String> createSuccessResponse(Object responseBody) {
        return ResponseEntity.status(200).body(toJson(responseBody));
    }

    default ResponseEntity<String> createErrorResponse(ApiErrorWrapper errorWrapper) {
        return ResponseEntity.status(errorWrapper.getErrorResponse().code()).body(toJson(errorWrapper));
    }

    private String toJson(Object object) {
        return Try.of(() -> objectMapper.writeValueAsString(object))
                .getOrElseThrow(e -> new RuntimeException("Cannot deserialize response", e));
    }
}
