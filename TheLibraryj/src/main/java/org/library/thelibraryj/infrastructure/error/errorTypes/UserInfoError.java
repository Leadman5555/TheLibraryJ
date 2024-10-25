package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserInfoError extends GeneralError{
    record UserInfoEntityNotFound(UUID missingEntityId) implements UserInfoError {}
    record UserAccountTooYoung(UUID userId, long accountAgeMissing) implements UserInfoError {}
    record UsernameNotUnique() implements UserInfoError {}
    record UsernameUpdateCooldown(long cooldownDurationLeft) implements UserInfoError {}
}
