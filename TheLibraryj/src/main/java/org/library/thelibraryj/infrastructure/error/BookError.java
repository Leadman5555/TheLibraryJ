package org.library.thelibraryj.infrastructure.error;

import java.util.UUID;

public sealed interface BookError extends GeneralError {
    record BookEntityNotFound(UUID missingEntityId, String optionalParam) implements BookError {}
}
