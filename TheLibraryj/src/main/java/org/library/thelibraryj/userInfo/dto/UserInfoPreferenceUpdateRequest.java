package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UserInfoPreferenceUpdateRequest(@Email String email, @Min(0) @Max(110) short preference) {
}
