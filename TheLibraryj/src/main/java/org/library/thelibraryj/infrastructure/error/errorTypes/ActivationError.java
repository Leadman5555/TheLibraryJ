package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface ActivationError extends GeneralError{
    record UserAlreadyEnabled(UUID userId) implements ActivationError {}
    record ActivationTokenNotFound(UUID tokenId) implements ActivationError {}
    record ActivationTokenExpired(UUID userId) implements ActivationError {}
    record ActivationTokenAlreadyUsed(UUID userId) implements ActivationError {}
}
