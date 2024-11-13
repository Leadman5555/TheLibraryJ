package org.library.thelibraryj.infrastructure.exception;

import jakarta.mail.MessagingException;
import org.library.thelibraryj.infrastructure.error.ApiErrorResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                        .message("Something on external layer went wrong. Request was nevertheless accepted and processed on server. Additional info: " + ex.getMessage())
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
                        .message("Something with email service went wrong: " + ex.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path("Path: " + extractRequest(request))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorWrapper> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Account with given email address does not exist.")
                        .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .path("Path: " + extractRequest(request))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiErrorWrapper> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        final String uri = extractRequest(request);
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.FORBIDDEN.value())
                        .message(uri.equals("/login") ? "Wrong password or email" : ex.getMessage())
                        .status(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .path("Path: " + uri)
                        .build()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({GoogleApiNotRespondingException.class})
    public ResponseEntity<ApiErrorWrapper> handleGoogleApiNotRespondingException(GoogleApiNotRespondingException ex, WebRequest request) {
        final ApiErrorWrapper error = new ApiErrorWrapper(
                ApiErrorResponse.builder()
                        .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .message("Google api not responding: " + ex.getMessage())
                        .status(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                        .path("Path: " + extractRequest(request))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    private static String extractRequest(WebRequest request) {
        return request instanceof ServletWebRequest svr ? svr.getRequest().getRequestURI() : "Unknown URL";
    }

}
