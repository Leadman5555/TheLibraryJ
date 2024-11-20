package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface ActivationError extends GeneralError{
    record UserAlreadyEnabled(String email) implements ActivationError {}
    record ActivationTokenNotFound(UUID tokenId) implements ActivationError {}
    record ActivationTokenExpired(UUID tokenId) implements ActivationError {}
    record ActivationTokenAlreadyUsed(UUID tokenId) implements ActivationError {}
}
