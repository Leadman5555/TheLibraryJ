package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface BookError extends GeneralError {
    record BookDetailEntityNotFound(UUID missingEntityId) implements BookError {}
    record BookPreviewEntityNotFound(UUID missingEntityId, String title) implements BookError {}
    record DuplicateTitleError() implements BookError {}
}
