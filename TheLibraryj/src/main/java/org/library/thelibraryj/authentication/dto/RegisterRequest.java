package org.library.thelibraryj.authentication.dto;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record RegisterRequest(String email,char[] password, String username,
                              @Nullable MultipartFile profileImage) {
}
