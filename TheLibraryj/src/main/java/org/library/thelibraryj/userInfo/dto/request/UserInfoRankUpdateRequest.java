package org.library.thelibraryj.userInfo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserInfoRankUpdateRequest(@NotBlank String email, @NotNull @Max(10) @Min(-10) Integer rankChange) {
}
