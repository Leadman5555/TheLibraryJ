package org.library.thelibraryj.infrastructure.exception;

import org.library.thelibraryj.infrastructure.error.ApiErrorResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LibraryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorWrapper> handleDefault(Exception ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({JsonDeserializationException.class})
    public ResponseEntity<ApiErrorWrapper> handleJsonDeserializationException(JsonDeserializationException ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path(ex.getEntityName())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
