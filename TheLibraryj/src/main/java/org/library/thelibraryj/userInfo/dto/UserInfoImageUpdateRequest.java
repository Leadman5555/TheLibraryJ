package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UserInfoImageUpdateRequest(@NotNull UUID userId, @NotNull MultipartFile newImage) {
}
