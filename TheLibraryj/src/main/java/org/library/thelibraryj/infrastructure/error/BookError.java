package org.library.thelibraryj.infrastructure.error;

import java.util.UUID;

public sealed interface BookError extends GeneralError {
    record BookDetailEntityNotFound(UUID missingEntityId) implements BookError {}
    record BookPreviewEntityNotFound(UUID missingEntityId) implements BookError {}
}
