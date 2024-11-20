package org.library.thelibraryj.userInfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UserInfoImageUpdateRequest(@NotBlank String email, @NotNull MultipartFile newImage) {
}
