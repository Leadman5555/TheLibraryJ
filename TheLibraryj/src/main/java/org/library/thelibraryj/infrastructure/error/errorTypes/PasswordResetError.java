package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface PasswordResetError extends GeneralError{
    record PasswordResetTokenNotFound(UUID tokenId) implements PasswordResetError {}
    record PasswordResetTokenExpired(UUID tokenId) implements PasswordResetError {}
    record PasswordResetTokenAlreadyUsed(UUID tokenId) implements PasswordResetError {}
}