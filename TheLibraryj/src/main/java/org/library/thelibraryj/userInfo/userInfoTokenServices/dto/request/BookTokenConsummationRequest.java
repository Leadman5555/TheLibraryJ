package org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request;

import jakarta.validation.constraints.Email;

import java.util.UUID;

public record BookTokenConsummationRequest(@org.hibernate.validator.constraints.UUID UUID token, @Email String email) {
}
