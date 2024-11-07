package org.library.thelibraryj.authentication.userAuth.dto;

import java.util.UUID;

public record BasicUserAuthData(UUID userAuthId, boolean isEnabled) {
}
