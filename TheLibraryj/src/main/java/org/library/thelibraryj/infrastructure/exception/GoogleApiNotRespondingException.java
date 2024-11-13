package org.library.thelibraryj.infrastructure.exception;

public class GoogleApiNotRespondingException extends RuntimeException {
    public GoogleApiNotRespondingException(String message) {
        super(message);
    }
}
