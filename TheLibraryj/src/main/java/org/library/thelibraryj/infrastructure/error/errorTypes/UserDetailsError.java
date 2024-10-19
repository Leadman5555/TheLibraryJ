package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserDetailsError extends GeneralError{
    record UserDetailsEntityNotFound(UUID missingEntityId) implements UserDetailsError {}
    record UserAccountTooYoung(UUID userId) implements UserDetailsError {}
}
