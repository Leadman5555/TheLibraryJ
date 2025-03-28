package org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request;

import jakarta.validation.constraints.Email;

public record BookTokenRequest(@Email String email) {
}
