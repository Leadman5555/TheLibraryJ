package org.library.thelibraryj.authentication.dto;

import jakarta.validation.constraints.Email;

public record AuthenticationRequest(@Email String email, char[] password){
}
