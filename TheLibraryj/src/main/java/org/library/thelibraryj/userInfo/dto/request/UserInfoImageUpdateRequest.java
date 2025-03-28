package org.library.thelibraryj.userInfo.dto.request;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record UserInfoImageUpdateRequest(String email, @Nullable MultipartFile newImage) {
}
