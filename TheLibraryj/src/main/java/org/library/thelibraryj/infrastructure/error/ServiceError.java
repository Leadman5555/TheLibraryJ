package org.library.thelibraryj.infrastructure.error;

public sealed interface ServiceError extends GeneralError {
    record DatabaseError(Throwable throwable) implements ServiceError {}
}
