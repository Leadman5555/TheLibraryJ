package org.library.thelibraryj.infrastructure.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.library.thelibraryj.infrastructure.error.ApiErrorResponse;
import org.library.thelibraryj.infrastructure.error.ApiErrorWrapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class LibraryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorWrapper> handleDefault(Exception ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message(ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path(extractRequest(request))
                .build();
        logServerError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler({JsonDeserializationException.class})
    public ResponseEntity<ApiErrorWrapper> handleJsonDeserializationException(JsonDeserializationException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Something on external layer went wrong. Request was nevertheless accepted and processed on server. Additional info: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request) + " For entity: " + ex.getEntityName())
                .build();
        logServerError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiErrorWrapper> handleMessageConstraintException(MessagingException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Error when sending mail message: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logServerError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorWrapper> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.NOT_FOUND;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Account with given email address does not exist.")
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiErrorWrapper> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        final String uri = extractRequest(request);
        HttpStatus errorStatus = HttpStatus.UNAUTHORIZED;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message(uri.equals("/login") ? "Wrong password or email" : ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + uri)
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler({GoogleApiNotRespondingException.class})
    public ResponseEntity<ApiErrorWrapper> handleGoogleApiNotRespondingException(GoogleApiNotRespondingException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.SERVICE_UNAVAILABLE;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Google api not responding. Message from Google api: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler(GoogleTokenVerificationException.class)
    public ResponseEntity<ApiErrorWrapper> handleGoogleTokenVerificationException(GoogleTokenVerificationException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.UNAUTHORIZED;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Google token invalid. Authorization failed: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApiErrorWrapper> handleJwtVerificationException(JWTVerificationException ex, WebRequest request) {
        HttpStatus errorStatus = (ex instanceof TokenExpiredException) ?  HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Authorization failed: Jwt verification failed. Reason: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler(RefreshTokenMissingException.class)
    public ResponseEntity<ApiErrorWrapper> handleRefreshTokenMissingException(RefreshTokenMissingException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Refresh token missing, cannot grant new access token: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorWrapper> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.FORBIDDEN;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("Authorization failed: Permission lacking. Reason: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiErrorWrapper> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
        HttpStatus errorStatus = HttpStatus.NOT_FOUND;
        final ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code(errorStatus.value())
                .message("View not found: " + ex.getMessage())
                .status(errorStatus.getReasonPhrase())
                .path("Path: " + extractRequest(request))
                .build();
        logError(errorResponse);
        return ResponseEntity.status(errorStatus).body(new ApiErrorWrapper(errorResponse));
    }

    private static void logError(ApiErrorResponse error) {
        log.info("{} at {}", error.message(), error.path());
    }

    private static void logServerError(ApiErrorResponse error) {
        log.error("{} at {}", error.message(), error.path());
    }

    private static String extractRequest(WebRequest request) {
        return request instanceof ServletWebRequest svr ? svr.getRequest().getRequestURI() : "Unknown URL";
    }

}
