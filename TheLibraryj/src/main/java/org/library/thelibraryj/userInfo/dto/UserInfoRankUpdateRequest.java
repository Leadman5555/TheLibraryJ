package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserInfoRankUpdateRequest(@NotNull UUID userId, @NotNull @Max(10) @Min(-10) Integer rankChange) {
}
