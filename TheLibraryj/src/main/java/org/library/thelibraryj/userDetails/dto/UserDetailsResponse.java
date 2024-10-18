package org.library.thelibraryj.userDetails.dto;

import java.util.UUID;

public record UserDetailsResponse(UUID userId, String username) {
}
