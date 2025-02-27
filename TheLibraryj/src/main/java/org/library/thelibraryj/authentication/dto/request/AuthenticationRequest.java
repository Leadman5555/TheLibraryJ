package org.library.thelibraryj.authentication.dto.request;

import jakarta.validation.constraints.Email;

public record AuthenticationRequest(@Email String email, char[] password){
}
