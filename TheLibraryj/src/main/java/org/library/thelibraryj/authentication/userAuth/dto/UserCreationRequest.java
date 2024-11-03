package org.library.thelibraryj.authentication.userAuth.dto;

public record UserCreationRequest(String email, char[] password, String username) {
}
