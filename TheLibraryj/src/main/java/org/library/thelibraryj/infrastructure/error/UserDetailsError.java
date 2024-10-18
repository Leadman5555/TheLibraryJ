package org.library.thelibraryj.infrastructure.error;

import java.util.UUID;

public sealed interface UserDetailsError extends GeneralError{
    record UserEntityNotFound(UUID missingEntityId) implements UserDetailsError {}
}
