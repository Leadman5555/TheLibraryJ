package org.library.thelibraryj.infrastructure.exception;

public class GoogleTokenVerificationException extends RuntimeException {
    public GoogleTokenVerificationException(String message) {
        super(message);
    }
}
