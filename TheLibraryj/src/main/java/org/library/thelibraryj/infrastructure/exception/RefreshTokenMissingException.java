package org.library.thelibraryj.infrastructure.exception;

public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException(String message) {
        super(message);
    }
}
