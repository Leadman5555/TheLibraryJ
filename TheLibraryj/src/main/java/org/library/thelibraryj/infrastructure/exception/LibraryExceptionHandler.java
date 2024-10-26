package org.library.thelibraryj.infrastructure.exception;

import jakarta.mail.MessagingException;
import org.library.thelibraryj.infrastructure.error.ApiErrorResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LibraryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorWrapper> handleDefault(Exception ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path(extractRequest(request))
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
                        .path("Path: " + extractRequest(request) + " For entity: " + ex.getEntityName())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiErrorWrapper> handleMessageConstraintException(MessagingException ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path("Path: " + extractRequest(request))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    private static String extractRequest(WebRequest request) {
        return request instanceof ServletWebRequest svr ?  svr.getRequest().getRequestURI() : "Unknown URL";
    }

}
