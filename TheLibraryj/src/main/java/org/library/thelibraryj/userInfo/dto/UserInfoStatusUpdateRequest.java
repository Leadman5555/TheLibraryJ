package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record UserInfoStatusUpdateRequest(@Email String email, @Nullable @Size(max = 300) String status) {
}
