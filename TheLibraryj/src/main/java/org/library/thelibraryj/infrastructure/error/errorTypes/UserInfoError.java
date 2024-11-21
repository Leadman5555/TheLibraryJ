package org.library.thelibraryj.infrastructure.error.errorTypes;

public sealed interface UserInfoError extends GeneralError{
    record UserInfoEntityNotFound(String missingEntityEmail) implements UserInfoError {}
    record UserInfoEntityNotFoundById() implements UserInfoError {}
    record UserInfoEntityNotFoundUsername(String missingEntityUsername) implements UserInfoError {}
    record UserAccountTooYoung(String userEmail, long accountAgeMissing) implements UserInfoError {}
    record UsernameNotUnique() implements UserInfoError {}
    record UsernameUpdateCooldown(long cooldownDurationLeft, String userEmail) implements UserInfoError {}
    record ProfileImageUpdateFailed() implements UserInfoError {}
    record UserNotEligibleForRankIncrease(String email, int missingScore) implements UserInfoError {}
}
