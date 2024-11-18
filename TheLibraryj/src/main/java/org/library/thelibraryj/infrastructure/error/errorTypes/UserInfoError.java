package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserInfoError extends GeneralError{
    record UserInfoEntityNotFound(String missingEntityEmail) implements UserInfoError {}
    record UserInfoEntityNotFoundById() implements UserInfoError {}
    record UserInfoEntityNotFoundUsername(String missingEntityUsername) implements UserInfoError {}
    record UserAccountTooYoung(String userEmail, long accountAgeMissing) implements UserInfoError {}
    record UsernameNotUnique() implements UserInfoError {}
    record UsernameUpdateCooldown(long cooldownDurationLeft) implements UserInfoError {}
    record ProfileImageUpdateFailed() implements UserInfoError {}
    record UserNotEligibleForRankIncrease(UUID userId, int missingScore) implements UserInfoError {}
}
