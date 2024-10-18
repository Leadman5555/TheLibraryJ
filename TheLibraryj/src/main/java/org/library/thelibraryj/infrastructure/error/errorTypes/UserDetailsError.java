package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserDetailsError extends GeneralError{
    record UserEntityNotFound(UUID missingEntityId) implements UserDetailsError {}
}
