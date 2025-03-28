package org.library.thelibraryj.infrastructure.exception;

import lombok.Getter;

@Getter
public class JsonDeserializationException extends RuntimeException {
    private final String entityName;
    public JsonDeserializationException(final String entityName) {
        super();
        this.entityName = entityName;
    }

}
