package org.library.thelibraryj.infrastructure.exception;

public class DockerSecretParsingException extends RuntimeException {
    public DockerSecretParsingException(String message) {
        super(message);
    }
}
