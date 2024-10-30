package org.library.thelibraryj.authentication.userAuth.dto;

import java.util.UUID;

public record UserAuthResponse(UUID id, String email) {
}
