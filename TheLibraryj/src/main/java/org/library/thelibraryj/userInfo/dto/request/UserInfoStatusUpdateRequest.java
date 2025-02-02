package org.library.thelibraryj.userInfo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record UserInfoStatusUpdateRequest(@Email String email, @Nullable @Size(max = 350) String status) {
}
