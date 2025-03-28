package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserAuthError extends GeneralError {
    record UserAuthNotFoundEmail(String email) implements UserAuthError {}
    record UserAuthNotFoundId(UUID userId) implements UserAuthError {}
    record UsernameNotUnique(String userEmail) implements UserAuthError {}
    record EmailNotUnique(String email) implements UserAuthError {}
    record UserNotEnabled(String email) implements UserAuthError {}
    record UserIsGoogleRegistered(String email) implements UserAuthError {}
}
