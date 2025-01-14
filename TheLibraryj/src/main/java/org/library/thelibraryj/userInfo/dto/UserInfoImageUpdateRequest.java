package org.library.thelibraryj.userInfo.dto;

import org.springframework.web.multipart.MultipartFile;

public record UserInfoImageUpdateRequest(String email, MultipartFile newImage) {
}
