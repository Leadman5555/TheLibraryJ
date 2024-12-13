package org.library.thelibraryj.infrastructure.error.errorTypes;

public sealed interface UserAuthError extends GeneralError {
    record UserAuthNotFoundEmail(String email) implements UserAuthError {}
    record UserAuthNotFoundId() implements UserAuthError {}
    record UsernameNotUnique(String username) implements UserAuthError {}
    record EmailNotUnique(String email) implements UserAuthError {}
    record UserNotEnabled(String email) implements UserAuthError {}
    record GoogleApiNotResponding() implements UserAuthError {}
    record UserIsGoogleRegistered(String email) implements UserAuthError {}
}
