package org.library.thelibraryj.authentication.userAuth.dto;

import java.util.UUID;

public record PasswordResetDataResponse(UUID userAuthId, boolean isGoogle) {
}
