package org.library.thelibraryj.infrastructure.error.errorTypes;

import java.util.UUID;

public sealed interface UserInfoError extends GeneralError{
    record UserInfoEntityNotFoundByEmail(String missingEntityEmail) implements UserInfoError {}
    record UserInfoEntityNotFoundById(UUID userId) implements UserInfoError {}
    record UserInfoEntityNotFoundByUsername(String missingEntityUsername) implements UserInfoError {}
    record UserAccountTooYoung(String userEmail, long accountAgeMissing) implements UserInfoError {}
    record UsernameNotUnique(String userEmail) implements UserInfoError {}
    record UsernameUpdateCooldown(long cooldownDurationLeft, String userEmail) implements UserInfoError {}
    record ProfileImageUpdateFailed(String userEmail) implements UserInfoError {}
    record UserNotEligibleForRankIncrease(String email, int missingScore) implements UserInfoError {}
    record UserNotEligibleForChosenPreference(String email, int missingRank) implements UserInfoError {}
}
